import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Brain, Send, User, Stethoscope, Calendar, ChevronRight,
  Sparkles, Loader2, AlertCircle, CheckCircle2
} from 'lucide-react';
import { rdvAPI, medecinAPI } from '../services/api';
import Sidebar from '../components/Sidebar';
import './AssistantIA.css';

/**
 * Assistant IA — Le clou du spectacle !
 * 
 * Cette page permet au patient de :
 * 1. Décrire ses symptômes dans un chat
 * 2. L'IA (Mistral) analyse et suggère une spécialité
 * 3. Le système trouve un médecin correspondant
 * 4. Proposition de prendre rendez-vous en un clic
 * 
 * Architecture :
 * Frontend → API Gateway → RDV Service → Mistral AI API
 *                              ↓
 *                        Medecin Service (recherche par spécialité)
 */

const AssistantIA = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('username') || 'Utilisateur';
  const role = localStorage.getItem('role') || 'PATIENT';
  
  // État du chat
  const [messages, setMessages] = useState([
    {
      id: 1,
      type: 'ai',
      content: 'Bonjour ! Je suis votre assistant médical intelligent. Décrivez-moi vos symptômes et je vous suggérerai la spécialité médicale adaptée ainsi qu\'un médecin disponible.',
      timestamp: new Date()
    }
  ]);
  
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [suggestion, setSuggestion] = useState(null);
  const messagesEndRef = useRef(null);

  // Scroll automatique vers le dernier message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Menu sidebar (adapté selon le rôle)
  const menuItems = role === 'MEDECIN' 
    ? [
        { icon: Brain, label: 'Assistant IA', active: true, path: '/assistant-ia' },
        { icon: User, label: 'Dashboard', path: '/dashboard-medecin' },
      ]
    : [
        { icon: Brain, label: 'Assistant IA', active: true, path: '/assistant-ia' },
        { icon: User, label: 'Dashboard', path: '/dashboard-patient' },
      ];

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    navigate('/login');
  };

  /**
   * Envoi du message et appel à l'IA
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!inputValue.trim() || loading) return;

    const userMessage = inputValue.trim();
    
    // Ajoute le message utilisateur au chat
    const newUserMsg = {
      id: Date.now(),
      type: 'user',
      content: userMessage,
      timestamp: new Date()
    };
    
    setMessages(prev => [...prev, newUserMsg]);
    setInputValue('');
    setLoading(true);
    setSuggestion(null);

    try {
      // ============================================
      // APPEL API À L'IA (LE MOMENT CLÉ !)
      // ============================================
      // Cet appel traverse : Gateway → RDV Service → Mistral AI
      const response = await rdvAPI.suggestMedecin(userMessage);
      const data = response.data;

      // Message de l'IA avec la suggestion
      const aiResponse = {
        id: Date.now() + 1,
        type: 'ai',
        content: `D'après vos symptômes, je vous recommande de consulter un **${data.specialiteSuggest}**.`,
        details: {
          specialite: data.specialiteSuggest,
          explication: data.explication,
          medecinId: data.medecinIdRecommande,
          medecinNom: data.medecinNomRecommande
        },
        timestamp: new Date()
      };

      setMessages(prev => [...prev, aiResponse]);
      setSuggestion({
        specialite: data.specialiteSuggest,
        explication: data.explication,
        medecinId: data.medecinIdRecommande,
        medecinNom: data.medecinNomRecommande
      });

    } catch (err) {
      // Message d'erreur de l'IA
      const errorMsg = {
        id: Date.now() + 1,
        type: 'ai',
        content: 'Désolé, je ne peux pas analyser vos symptômes pour le moment. Veuillez consulter un généraliste pour un premier avis médical.',
        isError: true,
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Prendre rendez-vous avec le médecin suggéré
   */
  const handlePrendreRDV = () => {
    if (suggestion?.medecinId) {
      navigate('/prendre-rdv', { 
        state: { 
          medecinId: suggestion.medecinId,
          medecinNom: suggestion.medecinNom,
          specialite: suggestion.specialite
        } 
      });
    }
  };

  return (
    <div className="dashboard">
      <Sidebar 
        menuItems={menuItems}
        userName={username}
        userRole={role}
        onLogout={handleLogout}
      />

      <main className="dashboard-main ia-page">
        {/* Header */}
        <header className="ia-header">
          <div>
            <h1>
              <Sparkles className="ia-icon" />
              Assistant Médical IA
            </h1>
            <p className="breadcrumb">
              Clinic+ <ChevronRight size={14} /> Assistant IA
            </p>
          </div>
          <div className="ia-badge-powered">
            <Brain size={14} />
            Propulsé par Mistral AI
          </div>
        </header>

        {/* Zone de chat */}
        <div className="chat-container">
          {/* Messages */}
          <div className="chat-messages">
            {messages.map((msg) => (
              <MessageBubble key={msg.id} message={msg} />
            ))}
            
            {/* Indicateur de chargement */}
            {loading && (
              <div className="message-ai loading">
                <div className="ai-avatar">
                  <Brain size={18} />
                </div>
                <div className="message-content">
                  <div className="typing-indicator">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                  <span className="loading-text">L'IA analyse vos symptômes...</span>
                </div>
              </div>
            )}
            
            <div ref={messagesEndRef} />
          </div>

          {/* Suggestion détaillée (apparaît après réponse IA) */}
          {suggestion && !loading && (
            <div className="suggestion-card">
              <div className="suggestion-header">
                <CheckCircle2 size={20} className="suggestion-check" />
                <h3>Suggestion de l'IA</h3>
              </div>
              
              <div className="suggestion-body">
                <div className="suggestion-item">
                  <Stethoscope size={18} />
                  <div>
                    <span className="label">Spécialité recommandée</span>
                    <span className="value">{suggestion.specialite}</span>
                  </div>
                </div>
                
                <div className="suggestion-item">
                  <AlertCircle size={18} />
                  <div>
                    <span className="label">Explication</span>
                    <span className="value text-sm">{suggestion.explication}</span>
                  </div>
                </div>
                
                {suggestion.medecinNom && (
                  <div className="suggestion-item">
                    <User size={18} />
                    <div>
                      <span className="label">Médecin recommandé</span>
                      <span className="value">{suggestion.medecinNom}</span>
                    </div>
                  </div>
                )}
              </div>

              <button 
                className="btn btn-primary btn-rdv"
                onClick={handlePrendreRDV}
              >
                <Calendar size={18} />
                Prendre rendez-vous
                <ChevronRight size={16} />
              </button>
            </div>
          )}

          {/* Input de saisie */}
          <form className="chat-input-container" onSubmit={handleSubmit}>
            <div className="chat-input-wrapper">
              <input
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="Décrivez vos symptômes... (ex: J'ai mal à la tête depuis 3 jours)"
                disabled={loading}
                className="chat-input"
              />
              <button 
                type="submit" 
                className="btn-send"
                disabled={loading || !inputValue.trim()}
              >
                {loading ? <Loader2 size={20} className="spin" /> : <Send size={20} />}
              </button>
            </div>
            <p className="chat-disclaimer">
              ⚠️ Cet assistant est un outil d'aide à la décision, pas un substitut à un avis médical professionnel.
            </p>
          </form>
        </div>

        {/* Exemples de symptômes (aide utilisateur) */}
        <div className="symptom-examples">
          <h4>Exemples de symptômes :</h4>
          <div className="example-chips">
            {[
              "J'ai des douleurs dans la poitrine",
              "Ma peau démange et j'ai des boutons",
              "Je vois flou depuis quelques jours",
              "J'ai mal à l'oreille et de la fièvre",
              "Je suis très fatigué et stressé"
            ].map((example, idx) => (
              <button
                key={idx}
                className="example-chip"
                onClick={() => setInputValue(example)}
                disabled={loading}
              >
                {example}
              </button>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
};

// ============================================
// SOUS-COMPOSANTS
// ============================================

/**
 * Bulle de message (utilisateur ou IA)
 */
const MessageBubble = ({ message }) => {
  const isUser = message.type === 'user';
  
  return (
    <div className={`message ${isUser ? 'message-user' : 'message-ai'}`}>
      {!isUser && (
        <div className="ai-avatar">
          <Brain size={18} />
        </div>
      )}
      
      <div className="message-content">
        <div className="message-text" dangerouslySetInnerHTML={{ 
          __html: message.content.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') 
        }} />
        
        {/* Détails de la suggestion (si présents) */}
        {message.details && (
          <div className="message-details">
            <div className="detail-row">
              <Stethoscope size={14} />
              <span>{message.details.specialite}</span>
            </div>
            {message.details.medecinNom && (
              <div className="detail-row">
                <User size={14} />
                <span>{message.details.medecinNom}</span>
              </div>
            )}
          </div>
        )}
        
        <span className="message-time">
          {message.timestamp.toLocaleTimeString('fr-FR', { 
            hour: '2-digit', 
            minute: '2-digit' 
          })}
        </span>
      </div>
    </div>
  );
};

export default AssistantIA;