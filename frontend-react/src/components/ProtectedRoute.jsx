import React from 'react';
import { Navigate } from 'react-router-dom';

/**
 * Composant de protection des routes.
 * 
 * Vérifie que l'utilisateur :
 * 1. A un token JWT (est connecté)
 * 2. A le bon rôle (si spécifié)
 * 
 * Si non connecté → redirige vers /login
 * Si mauvais rôle → redirige vers le bon dashboard
 */

const ProtectedRoute = ({ children, allowedRole }) => {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('role');

  // Pas de token = non connecté → login
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // Vérification du rôle (si une restriction est définie)
  if (allowedRole && userRole !== allowedRole) {
    // Mauvais rôle → redirige vers le dashboard approprié
    if (userRole === 'MEDECIN') {
      return <Navigate to="/dashboard-medecin" replace />;
    } else {
      return <Navigate to="/dashboard-patient" replace />;
    }
  }

  // Tout est OK → affiche la page demandée
  return children;
};

export default ProtectedRoute;