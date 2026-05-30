import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// ============================================
// INTERCEPTEUR DE RÉPONSES — VERSION DÉSACTIVÉE
// ============================================

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // DÉSACTIVÉ : ne redirige plus automatiquement vers login
    // if (error.response && error.response.status === 401) {
    //   localStorage.removeItem('token');
    //   window.location.href = '/login';
    // }
    
    // À la place : log l'erreur mais ne redirige pas
    if (error.response && error.response.status === 401) {
      console.warn('Erreur 401 - Token invalide, mais navigation non bloquée pour la démo');
    }
    
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => api.post('/api/auth/login', credentials),
};

export const patientAPI = {
  getAll: () => api.get('/api/patients'),
  create: (data) => api.post('/api/patients', data),
  update: (id, data) => api.put(`/api/patients/${id}`, data),
  delete: (id) => api.delete(`/api/patients/${id}`),
};

export const medecinAPI = {
  getAll: () => api.get('/api/medecins'),
  getBySpecialite: (specialite) => api.get(`/api/medecins/specialite?specialite=${specialite}`),
};

export const rdvAPI = {
  getAll: () => api.get('/api/rendezvous'),
  create: (data) => api.post('/api/rendezvous', data),
  getByPatient: (patientId) => api.get(`/api/rendezvous/patient/${patientId}`),
  suggestMedecin: (symptomes) => api.get(`/api/rendezvous/suggestion?symptomes=${encodeURIComponent(symptomes)}`),
};

export default api;