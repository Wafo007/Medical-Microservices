import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  User, Calendar, Brain, ChevronRight, Clock, 
  AlertCircle, Loader2, Heart, Shield, Phone
} from 'lucide-react';
import { rdvAPI, patientAPI } from '../services/api';
import Sidebar from '../components/Sidebar';
import './DashboardPatient.css';

/**
 * Dashboard Patient — Vue personnelle du patient.
 * 
 * Contrairement au médecin qui voit TOUT, le patient ne voit que :
 * - Ses propres informations
 * - Ses rendez-vous à venir
 * - L'accès à l'assistant IA
 * 
 * C'est la démonstration de la sécurité par rôle !
 */

const DashboardPatient = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('username') || 'Patient';
  
  // État des données
  const [patientInfo, setPatientInfo] = useState(null);
  const [rendezVous, setRendezVous] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Menu sidebar (limité pour le patient)
  const menuItems = [
    { icon: User, label: 'Mon Espace', active: true, path: '/dashboard-patient' },
    { icon: Calendar, label: 'Mes Rendez-vous', path: '/rendezvous' },
    { icon: Calendar, label: 'Prendre RDV', path: '/prendre-rdv' },
    { icon: Brain, label: 'Assistant IA', path: '/assistant-ia' },
  ];

  // Chargement des données du patient
  useEffect(() => {
    fetchPatientData();
  }, []);

  const fetchPatientData = async () => {
    try {
      setLoading(true);
      
      // Récupère tous les patients et trouve celui qui correspond au username
      // (En production, on aurait un endpoint /api/patients/me)
      const patientsRes = await patientAPI.getAll();
      const currentPatient = patientsRes.data.find(p => 
        p.nom.toLowerCase() === username.split('.')[1] || 
        p.prenom.toLowerCase() === username.split('.')[0]
      );
      
      if (currentPatient) {
        setPatientInfo(currentPatient);
        
        // Récupère les RDV de ce patient
        const rdvRes = await rdvAPI.getByPatient(currentPatient.id);
        setRendezVous(rdvRes.data);
      }
      
    } catch (err) {
      setError('Impossible de charger vos informations.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  // Prochains rendez-vous (triés par date)
  const upcomingRDV = rendezVous
    .filter(r => new Date(r.date) >= new Date())
    .sort((a, b) => new Date(a.date) - new Date(b.date))
    .slice(0, 3);

  return (
    <div className="dashboard">
      <Sidebar 
        menuItems={menuItems}
        userName={username}
        userRole="Patient"
        onLogout={handleLogout}
      />

      <main className="dashboard-main patient-dashboard">
        {/* Header personnalisé */}
        <header className="patient-header">
          <div className="patient-welcome">
            <div className="welcome-avatar">
              {patientInfo?.prenom?.charAt(0) || username.charAt(0).toUpperCase()}
            </div>
            <div className="welcome-text">
              <h1>Bonjour, {patientInfo?.prenom || username} !</h1>
              <p>Bienvenue dans votre espace personnel Clinic+</p>
            </div>
          </div>
          <div className="patient-badge">
            <Shield size={16} />
            Espace sécurisé
          </div>
        </header>

        {error && (
          <div className="alert alert-error">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        {loading ? (
          <div className="loading-state">
            <Loader2 size={32} className="spin" />
            <p>Chargement de vos données...</p>
          </div>
        ) : (
          <>
            {/* Grille d'informations */}
            <div className="patient-grid">
              {/* Carte : Mes informations */}
              <section className="patient-card info-card">
                <div className="card-header">
                  <User size={20} />
                  <h2>Mes Informations</h2>
                </div>
                
                {patientInfo ? (
                  <div className="info-list">
                    <InfoRow icon={User} label="Nom" value={`${patientInfo.prenom} ${patientInfo.nom}`} />
                    <InfoRow icon={Calendar} label="Date de naissance" value={formatDate(patientInfo.dateNaissance)} />
                    <InfoRow icon={Phone} label="Téléphone" value={patientInfo.telephone || 'Non renseigné'} />
                    <InfoRow icon={Heart} label="N° Sécurité Sociale" value={patientInfo.numeroSecu} />
                  </div>
                ) : (
                  <p className="no-data">Informations non disponibles</p>
                )}
              </section>

              {/* Carte : Prochains rendez-vous */}
              <section className="patient-card rdv-card">
                <div className="card-header">
                  <Calendar size={20} />
                  <h2>Mes Rendez-vous</h2>
                </div>
                
                {upcomingRDV.length > 0 ? (
                  <div className="rdv-list-patient">
                    {upcomingRDV.map((rdv, idx) => (
                      <div key={idx} className="rdv-item-patient">
                        <div className="rdv-date-badge">
                          <span className="rdv-day">{new Date(rdv.date).getDate()}</span>
                          <span className="rdv-month">
                            {new Date(rdv.date).toLocaleDateString('fr-FR', { month: 'short' })}
                          </span>
                        </div>
                        <div className="rdv-details">
                          <span className="rdv-time">
                            <Clock size={14} /> {rdv.heureDebut}
                          </span>
                          <span className="rdv-medecin">
                            Dr. {rdv.medecin?.nom || 'Médecin'}
                          </span>
                          <span className="rdv-motif">{rdv.motif}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="no-rdv">
                    <Calendar size={32} className="no-rdv-icon" />
                    <p>Aucun rendez-vous à venir</p>
                    <button 
                      className="btn btn-primary btn-sm"
                      onClick={() => navigate('/prendre-rdv')}
                    >
                      Prendre rendez-vous
                    </button>
                  </div>
                )}
              </section>

              {/* Carte : Actions rapides */}
              <section className="patient-card actions-card">
                <div className="card-header">
                  <Heart size={20} />
                  <h2>Actions Rapides</h2>
                </div>
                
                <div className="quick-actions-patient">
                  <QuickAction 
                    icon={Calendar}
                    title="Prendre un RDV"
                    desc="Réserver une consultation"
                    color="blue"
                    onClick={() => navigate('/prendre-rdv')}
                  />
                  <QuickAction 
                    icon={Brain}
                    title="Assistant IA"
                    desc="Décrivez vos symptômes"
                    color="purple"
                    onClick={() => navigate('/assistant-ia')}
                  />
                </div>
              </section>

              {/* Carte : Sécurité */}
              <section className="patient-card security-card">
                <div className="card-header">
                  <Shield size={20} />
                  <h2>Sécurité</h2>
                </div>
                
                <div className="security-info">
                  <div className="security-item">
                    <Shield size={18} className="security-icon" />
                    <div>
                      <span className="security-label">Authentification</span>
                      <span className="security-value">JWT active</span>
                    </div>
                  </div>
                  <div className="security-item">
                    <Shield size={18} className="security-icon" />
                    <div>
                      <span className="security-label">Accès aux données</span>
                      <span className="security-value">Restreint à votre profil</span>
                    </div>
                  </div>
                  <p className="security-note">
                    Vos données médicales sont protégées et accessibles uniquement par vous et vos médecins.
                  </p>
                </div>
              </section>
            </div>
          </>
        )}
      </main>
    </div>
  );
};

// ============================================
// SOUS-COMPOSANTS
// ============================================

const InfoRow = ({ icon: Icon, label, value }) => (
  <div className="info-row">
    <Icon size={16} className="info-icon" />
    <div className="info-content">
      <span className="info-label">{label}</span>
      <span className="info-value">{value}</span>
    </div>
  </div>
);

const QuickAction = ({ icon: Icon, title, desc, color, onClick }) => {
  const colors = {
    blue: 'action-blue',
    purple: 'action-purple',
    green: 'action-green'
  };

  return (
    <button className={`quick-action-patient ${colors[color]}`} onClick={onClick}>
      <div className="action-icon-patient">
        <Icon size={20} />
      </div>
      <div className="action-text-patient">
        <span className="action-title">{title}</span>
        <span className="action-desc">{desc}</span>
      </div>
      <ChevronRight size={16} />
    </button>
  );
};

const formatDate = (isoDate) => {
  if (!isoDate) return '—';
  return new Date(isoDate).toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric'
  });
};

export default DashboardPatient;