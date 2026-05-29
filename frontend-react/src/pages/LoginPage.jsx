import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Heart, Eye, EyeOff, Lock, User, ArrowRight } from 'lucide-react';
import { authAPI } from '../services/api';
import './AuthPages.css';

/**
 * Page de connexion avec JWT.
 * 
 * Processus :
 * 1. L'utilisateur saisit login/password
 * 2. Appel API POST /api/auth/login
 * 3. Stockage du token dans localStorage
 * 4. Redirection vers le dashboard selon le rôle
 */

const LoginPage = () => {
  const navigate = useNavigate();
  
  // État du formulaire
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  
  // État UI
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Gestion des changements dans les champs
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError(''); // Efface l'erreur quand l'utilisateur tape
  };

  // Soumission du formulaire
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Appel API de connexion
      const response = await authAPI.login(formData);
      const { token, role, username } = response.data;

      // Stockage dans le navigateur (persistant même après fermeture)
      localStorage.setItem('token', token);
      localStorage.setItem('role', role);
      localStorage.setItem('username', username);

      // Redirection selon le rôle
      if (role === 'MEDECIN') {
        navigate('/dashboard-medecin');
      } else {
        navigate('/dashboard-patient');
      }

    } catch (err) {
      setError(
        err.response?.data?.message || 
        'Erreur de connexion. Vérifiez vos identifiants.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        {/* Logo et titre */}
        <div className="auth-header">
          <div className="auth-logo">
            <Heart className="logo-icon" />
            <span>Clinic<span className="logo-plus">+</span></span>
          </div>
          <h1>Connexion</h1>
          <p>Accédez à votre espace médical sécurisé</p>
        </div>

        {/* Formulaire */}
        <form onSubmit={handleSubmit} className="auth-form">
          {error && (
            <div className="auth-error">
              {error}
            </div>
          )}

          {/* Champ username */}
          <div className="form-group">
            <label>Nom d'utilisateur</label>
            <div className="input-wrapper">
              <User size={18} className="input-icon" />
              <input
                type="text"
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="dr.martin"
                required
                autoFocus
              />
            </div>
          </div>

          {/* Champ password */}
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
          </div>

          {/* Bouton de connexion */}
          <button 
            type="submit" 
            className="btn btn-primary btn-full"
            disabled={loading}
          >
            {loading ? 'Connexion...' : (
              <>Se connecter <ArrowRight size={18} /></>
            )}
          </button>
        </form>

        {/* Lien vers inscription */}
        <div className="auth-footer">
          <p>Pas encore de compte ?</p>
          <Link to="/register" className="auth-link">
            Créer un compte patient
          </Link>
        </div>

        {/* Comptes de démo */}
        <div className="demo-accounts">
          <p>Comptes de démonstration :</p>
          <div className="demo-list">
            <span><strong>dr.martin</strong> / password123 (Médecin)</span>
            <span><strong>jean.dupont</strong> / password456 (Patient)</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;