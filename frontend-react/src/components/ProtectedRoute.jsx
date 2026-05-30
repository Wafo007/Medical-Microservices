import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, allowedRole }) => {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('role');

  console.log('ProtectedRoute - Token:', token ? 'présent' : 'absent'); // DEBUG
  console.log('ProtectedRoute - Role:', userRole); // DEBUG

  // Pas de token = non connecté → login
  if (!token) {
    console.log('ProtectedRoute: Pas de token, redirection vers login');
    return <Navigate to="/login" replace />;
  }

  // Vérification du rôle (si une restriction est définie)
  if (allowedRole && userRole !== allowedRole) {
    console.log('ProtectedRoute: Mauvais rôle, redirection'); // DEBUG
    if (userRole === 'MEDECIN') {
      return <Navigate to="/dashboard-medecin" replace />;
    } else {
      return <Navigate to="/dashboard-patient" replace />;
    }
  }

  // Tout est OK → affiche la page demandée
  console.log('ProtectedRoute: Accès autorisé');
  return children;
};

export default ProtectedRoute;