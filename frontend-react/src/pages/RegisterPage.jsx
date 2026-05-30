import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  Heart, Eye, EyeOff, Lock, User, Mail, Phone, 
  ArrowLeft, CheckCircle, AlertCircle
} from 'lucide-react';
import './AuthPages.css';

/**
 * Page d'inscription — Création de compte patient.
 * 
 * Cette page permet à un nouveau patient de :
 * 1. Saisir ses informations personnelles
 * 2. Choisir un nom d'utilisateur et un mot de passe
 * 3. Créer son compte dans le système
 * 
 * NOTE : Pour le devoir, l'inscription crée uniquement un compte
 * dans la table `users`. En production, on créerait aussi un
 * enregistrement dans la table `patients`.
 */

const RegisterPage = () => {
  const navigate = useNavigate();
  
  // État du formulaire
  const [formData, setFormData] = useState({
    fullName: '',
    username: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: ''
  });
  
  // État UI
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Gestion des changements dans les champs
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
    setSuccess('');
  };

  // Validation du formulaire
  const validateForm = () => {
    if (!formData.fullName.trim()) {
      setError('Le nom complet est requis');
      return false;
    }
    if (!formData.username.trim()) {
      setError('Le nom d\'utilisateur est requis');
      return false;
    }
    if (formData.username.length < 3) {
      setError('Le nom d\'utilisateur doit contenir au moins 3 caractères');
      return false;
    }
    if (!formData.email.trim()) {
      setError('L\'email est requis');
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('L\'email n\'est pas valide');
      return false;
    }
    if (!formData.password) {
      setError('Le mot de passe est requis');
      return false;
    }
    if (formData.password.length < 6) {
      setError('Le mot de passe doit contenir au moins 6 caractères');
      return false;
    }
    if (formData.password !== formData.confirmPassword) {
      setError('Les mots de passe ne correspondent pas');
      return false;
    }
    return true;
  };

  // Soumission du formulaire
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      // ============================================
      // APPEL API D'INSCRIPTION
      // ============================================
      // NOTE : Pour le devoir, on simule l'inscription
      // car ton backend n'a pas encore d'endpoint /api/auth/register
      // 
      // En production, ce serait :
      // await authAPI.register(formData);
      
      // Simulation d'inscription réussie
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      setSuccess('Compte créé avec succès ! Vous pouvez maintenant vous connecter.');
      
      // Redirection vers login après 2 secondes
      setTimeout(() => {
        navigate('/login');
      }, 2000);

    } catch (err) {
      setError(
        err.response?.data?.message || 
        'Erreur lors de l\'inscription. Veuillez réessayer.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container register-container">
        {/* Bouton retour */}
        <button 
          className="btn-back"
          onClick={() => navigate('/')}
        >
          <ArrowLeft size={18} />
          Retour à l'accueil
        </button>

        {/* Logo et titre */}
        <div className="auth-header">
          <div className="auth-logo">
            <Heart className="logo-icon" />
            <span>Clinic<span className="logo-plus">+</span></span>
          </div>
          <h1>Créer un compte</h1>
          <p>Rejoignez Clinic+ pour gérer vos rendez-vous médicaux</p>
        </div>

        {/* Formulaire */}
        <form onSubmit={handleSubmit} className="auth-form">
          {/* Message de succès */}
          {success && (
            <div className="auth-success">
              <CheckCircle size={18} />
              {success}
            </div>
          )}

          {/* Message d'erreur */}
          {error && (
            <div className="auth-error">
              <AlertCircle size={18} />
              {error}
            </div>
          )}

          {/* Nom complet */}
          <div className="form-group">
            <label>Nom complet</label>
            <div className="input-wrapper">
              <User size={18} className="input-icon" />
              <input
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                placeholder="Jean Dupont"
                required
                autoFocus
              />
            </div>
          </div>

          {/* Nom d'utilisateur */}
          <div className="form-group">
            <label>Nom d'utilisateur</label>
            <div className="input-wrapper">
              <User size={18} className="input-icon" />
              <input
                type="text"
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="jean.dupont"
                required
              />
            </div>
            <span className="field-hint">Choisissez un nom unique pour vous connecter</span>
          </div>

          {/* Email */}
          <div className="form-group">
            <label>Email</label>
            <div className="input-wrapper">
              <Mail size={18} className="input-icon" />
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="jean.dupont@email.com"
                required
              />
            </div>
          </div>

          {/* Téléphone */}
          <div className="form-group">
            <label>Téléphone</label>
            <div className="input-wrapper">
              <Phone size={18} className="input-icon" />
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="06 12 34 56 78"
              />
            </div>
          </div>

          {/* Mot de passe */}
          <div className="form-group">
            <label>Mot de passe</label>
            <div className="input-wrapper">
              <Lock size={18} className="input-icon" />
              <input
                type={showPassword ? 'text' : 'password'}
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="••••••••"
                required
              />
              <button
                type="button"
                className="toggle-password"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            <span className="field-hint">Minimum 6 caractères</span>
          </div>

          {/* Confirmation mot de passe */}
          <div className="form-group">
            <label>Confirmer le mot de passe</label>
            <div className="input-wrapper">
              <Lock size={18} className="input-icon" />
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="••••••••"
                required
              />
              <button
                type="button"
                className="toggle-password"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                {showConfirmPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          {/* Bouton d'inscription */}
          <button 
            type="submit" 
            className="btn btn-primary btn-full"
            disabled={loading || success}
          >
            {loading ? 'Création du compte...' : 'Créer mon compte'}
          </button>
        </form>

        {/* Lien vers connexion */}
        <div className="auth-footer">
          <p>Déjà un compte ?</p>
          <Link to="/login" className="auth-link">
            Se connecter
          </Link>
        </div>

        {/* Informations */}
        <div className="register-info">
          <p>
            En créant un compte, vous acceptez nos conditions d'utilisation.
            Vos données sont sécurisées et ne seront jamais partagées.
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;