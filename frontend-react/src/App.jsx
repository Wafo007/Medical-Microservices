import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardMedecin from './pages/DashboardMedecin';
import DashboardPatient from './pages/DashboardPatient';
import PatientsPage from './pages/PatientsPage';
import MedecinsPage from './pages/MedecinsPage';
import RendezVousPage from './pages/RendezVousPage';
import PrendreRDVPage from './pages/PrendreRDVPage';
import AssistantIA from './pages/AssistantIA';
import ProtectedRoute from './components/ProtectedRoute';

/**
 * Routeur principal de l'application.
 * 
 * Définit toutes les URLs et les composants associés.
 * Les routes protégées vérifient le JWT avant d'afficher la page.
 */

function App() {
  return (
    <Router>
      <Routes>
        {/* Routes publiques */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Routes protégées - MEDECIN */}
        <Route 
          path="/dashboard-medecin" 
          element={
            <ProtectedRoute allowedRole="MEDECIN">
              <DashboardMedecin />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/patients" 
          element={
            <ProtectedRoute allowedRole="MEDECIN">
              <PatientsPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/medecins" 
          element={
            <ProtectedRoute allowedRole="MEDECIN">
              <MedecinsPage />
            </ProtectedRoute>
          } 
        />

        {/* Routes protégées - PATIENT */}
        <Route 
          path="/dashboard-patient" 
          element={
            <ProtectedRoute allowedRole="PATIENT">
              <DashboardPatient />
            </ProtectedRoute>
          } 
        />

        {/* Routes protégées - Les deux rôles */}
        <Route 
          path="/rendezvous" 
          element={
            <ProtectedRoute>
              <RendezVousPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/prendre-rdv" 
          element={
            <ProtectedRoute>
              <PrendreRDVPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/assistant-ia" 
          element={
            <ProtectedRoute>
              <AssistantIA />
            </ProtectedRoute>
          } 
        />
      </Routes>
    </Router>
  );
}

export default App;