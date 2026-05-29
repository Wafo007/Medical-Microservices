/**
 * Client HTTP Axios configuré pour communiquer avec l'API Gateway.
 * 
 * Ce fichier est le PONT entre le frontend React et le backend Spring Boot.
 * Il gère automatiquement :
 * - L'URL de base (la Gateway sur port 8080)
 * - L'envoi du token JWT dans chaque requête
 * - La redirection vers login si le token expire
 */

import axios from 'axios';

// ============================================
// CONFIGURATION DE BASE
// ============================================
// L'API Gateway est le SEUL point d'entrée.
// Tous les appels passent par elle, elle route vers les bons services.

const api = axios.create({
  baseURL: 'http://localhost:8080',  // URL de la Gateway
  headers: {
    'Content-Type': 'application/json',
  },
});

// ============================================
// INTERCEPTEUR DE REQUÊTES
// ============================================
// Avant CHAQUE requête sortante, on ajoute le token JWT dans le header.
// C'est comme présenter son badge à chaque porte.

api.interceptors.request.use(
  (config) => {
    // Récupère le token stocké dans le navigateur (localStorage)
    const token = localStorage.getItem('token');
    
    if (token) {
      // Ajoute le header Authorization: Bearer <token>
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    // Si une erreur survient avant l'envoi
    return Promise.reject(error);
  }
);

// ============================================
// INTERCEPTEUR DE RÉPONSES
// ============================================
// Quand le serveur répond, on vérifie s'il y a une erreur 401 (non autorisé).
// Si oui, le token a expiré ou est invalide → on déconnecte l'utilisateur.

api.interceptors.response.use(
  (response) => {
    // Réponse OK, on la retourne telle quelle
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Redirection vers login (on utilisera window.location pour simplifier)
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ============================================
// FONCTIONS API PRINCIPALES
// ============================================

export const authAPI = {
  /** Connexion : envoie login/password, reçoit token JWT */
  login: (credentials) => api.post('/api/auth/login', credentials),
};

export const patientAPI = {
  /** Récupère tous les patients (rôle MEDECIN uniquement) */
  getAll: () => api.get('/api/patients'),
  /** Crée un nouveau patient */
  create: (data) => api.post('/api/patients', data),
  /** Modifie un patient */
  update: (id, data) => api.put(`/api/patients/${id}`, data),
  /** Supprime un patient */
  delete: (id) => api.delete(`/api/patients/${id}`),
};

export const medecinAPI = {
  /** Récupère tous les médecins */
  getAll: () => api.get('/api/medecins'),
  /** Recherche par spécialité (utilisé par l'IA) */
  getBySpecialite: (specialite) => api.get(`/api/medecins/specialite?specialite=${specialite}`),
};

export const rdvAPI = {
  /** Récupère tous les rendez-vous */
  getAll: () => api.get('/api/rendezvous'),
  /** Crée un rendez-vous */
  create: (data) => api.post('/api/rendezvous', data),
  /** Récupère les RDV d'un patient */
  getByPatient: (patientId) => api.get(`/api/rendezvous/patient/${patientId}`),
  /** Appelle l'IA pour suggérer un médecin */
  suggestMedecin: (symptomes) => api.get(`/api/rendezvous/suggestion?symptomes=${encodeURIComponent(symptomes)}`),
};

export default api;