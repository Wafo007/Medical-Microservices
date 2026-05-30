import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  LayoutDashboard, Users, Stethoscope, Calendar, Brain, 
  LogOut, ChevronRight, Activity, TrendingUp, Clock,
  AlertCircle, CheckCircle, UserPlus
} from 'lucide-react';
import { patientAPI, rdvAPI } from '../services/api';
import Sidebar from '../components/Sidebar';
import './Dashboard.css';

/**
 * Dashboard Médecin — Page centrale du médecin connecté.
 * 
 * Cette page est le "cockpit" du médecin. Elle affiche :
 * - Statistiques clés (nombre de patients, RDV du jour, etc.)
 * - Liste des prochains rendez-vous
 * - Accès rapide aux fonctionnalités principales
 * 
 * Architecture : appelle l'API Gateway qui route vers :
 * - Patient Service (pour le nombre de patients)
 * - RDV Service (pour les rendez-vous du jour)
 */

const DashboardMedecin = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('username') || 'Médecin';
  
  // État des données
  const [stats, setStats] = useState({
    totalPatients: 0,
    rdvToday: 0,
    rdvWeek: 0,
    newPatients: 0
  });
  
  const [recentRDV, setRecentRDV] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Chargement des données au montage du composant
  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Appels parallèles pour optimiser les performances
      const [patientsRes, rdvRes] = await Promise.all([
        patientAPI.getAll(),
        rdvAPI.getAll()
      ]);

      const patients = patientsRes.data;
      const rdvs = rdvRes.data;

      // Filtrer les RDV du jour (date actuelle)
      const today = new Date().toISOString().split('T')[0];
      const rdvToday = rdvs.filter(r => r.date === today);
      
      // Calculer les stats
      setStats({
        totalPatients: patients.length,
        rdvToday: rdvToday.length,
        rdvWeek: rdvs.length, // Simplifié pour le devoir
        newPatients: patients.filter(p => {
          const date = new Date(p.dateNaissance);
          return (new Date() - date) < 30 * 24 * 60 * 60 * 1000; // 30 jours
        }).length
      });

      // Prochains RDV (triés par date et heure)
      setRecentRDV(rdvs.slice(0, 5));

    } catch (err) {
      setError('Impossible de charger les données du dashboard');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };


  //Code temporaire 
  // DEBUG TEMPORAIRE - À supprimer après correction
useEffect(() => {
  console.log('=== DEBUG TOKEN ===');
  console.log('Token dans localStorage:', localStorage.getItem('token'));
  console.log('Role dans localStorage:', localStorage.getItem('role'));
  console.log('Username dans localStorage:', localStorage.getItem('username'));
  
  // Vérifier si le token est un JWT valide (3 parties séparées par des points)
  const token = localStorage.getItem('token');
  if (token) {
    const parts = token.split('.');
    console.log('Nombre de parties du token:', parts.length); // Doit être 3
    
    if (parts.length === 3) {
      try {
        // Décoder le payload (partie du milieu)
        const payload = JSON.parse(atob(parts[1]));
        console.log('Payload du token:', payload);
        console.log('Expiration (exp):', new Date(payload.exp * 1000));
        console.log('Date actuelle:', new Date());
        console.log('Token expiré?', payload.exp * 1000 < Date.now());
      } catch (e) {
        console.error('Erreur décodage token:', e);
      }
    }
  }
  console.log('===================');
}, []);

  // Déconnexion
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    navigate('/login');
  };

  // Menu de navigation pour la sidebar
  const menuItems = [
    { icon: LayoutDashboard, label: 'Dashboard', active: true, path: '/dashboard-medecin' },
    { icon: Users, label: 'Patients', path: '/patients' },
    { icon: Stethoscope, label: 'Médecins', path: '/medecins' },
    { icon: Calendar, label: 'Rendez-vous', path: '/rendezvous' },
    { icon: Brain, label: 'Assistant IA', path: '/assistant-ia' },
  ];

  return (
    <div className="dashboard">
      {/* Sidebar de navigation */}
      <Sidebar 
        menuItems={menuItems} 
        userName={username} 
        userRole="Médecin"
        onLogout={handleLogout}
      />

      {/* Contenu principal */}
      <main className="dashboard-main">
        {/* Header */}
        <header className="dashboard-header">
          <div>
            <h1>Tableau de bord</h1>
            <p className="breadcrumb">
              Clinic+ <ChevronRight size={14} /> Dashboard
            </p>
          </div>
          <div className="header-actions">
            <button className="btn btn-outline" onClick={() => navigate('/prendre-rdv')}>
              <Calendar size={16} /> Nouveau RDV
            </button>
          </div>
        </header>

        {/* Message d'erreur */}
        {error && (
          <div className="alert alert-error">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        {/* Grille de statistiques */}
        <section className="stats-grid">
          <StatCard 
            icon={Users}
            label="Total Patients"
            value={stats.totalPatients}
            trend="+12%"
            color="blue"
            loading={loading}
          />
          <StatCard 
            icon={Calendar}
            label="RDV Aujourd'hui"
            value={stats.rdvToday}
            trend="En cours"
            color="green"
            loading={loading}
          />
          <StatCard 
            icon={Activity}
            label="RDV Cette Semaine"
            value={stats.rdvWeek}
            trend="+5"
            color="purple"
            loading={loading}
          />
          <StatCard 
            icon={UserPlus}
            label="Nouveaux Patients"
            value={stats.newPatients}
            trend="Ce mois"
            color="orange"
            loading={loading}
          />
        </section>

        {/* Section principale : 2 colonnes */}
        <div className="dashboard-content">
          {/* Colonne gauche : Prochains RDV */}
          <section className="content-card">
            <div className="card-header">
              <h2>
                <Clock size={20} />
                Prochains Rendez-vous
              </h2>
              <button 
                className="btn btn-text"
                onClick={() => navigate('/rendezvous')}
              >
                Voir tout <ChevronRight size={16} />
              </button>
            </div>
            
            <div className="rdv-list">
              {loading ? (
                <div className="skeleton-loader">Chargement...</div>
              ) : recentRDV.length === 0 ? (
                <div className="empty-state">
                  <Calendar size={48} className="empty-icon" />
                  <p>Aucun rendez-vous programmé</p>
                  <button 
                    className="btn btn-primary"
                    onClick={() => navigate('/prendre-rdv')}
                  >
                    Prendre un rendez-vous
                  </button>
                </div>
              ) : (
                recentRDV.map((rdv, index) => (
                  <RDVItem key={rdv.id || index} rdv={rdv} />
                ))
              )}
            </div>
          </section>

          {/* Colonne droite : Accès rapide */}
          <section className="content-card">
            <div className="card-header">
              <h2>
                <TrendingUp size={20} />
                Accès Rapide
              </h2>
            </div>
            
            <div className="quick-actions">
              <QuickAction 
                icon={Users}
                title="Gérer les Patients"
                description="Liste complète, ajout, modification"
                color="blue"
                onClick={() => navigate('/patients')}
              />
              <QuickAction 
                icon={Brain}
                title="Assistant IA"
                description="Suggestion de spécialité selon symptômes"
                color="purple"
                onClick={() => navigate('/assistant-ia')}
              />
              <QuickAction 
                icon={Calendar}
                title="Planning"
                description="Vue calendrier des consultations"
                color="green"
                onClick={() => navigate('/rendezvous')}
              />
              <QuickAction 
                icon={Stethoscope}
                title="Annuaire Médecins"
                description="Spécialités et disponibilités"
                color="orange"
                onClick={() => navigate('/medecins')}
              />
            </div>
          </section>
        </div>
      </main>
    </div>
  );
};

// ============================================
// SOUS-COMPOSANTS
// ============================================

/**
 * Carte de statistique avec icône, valeur et tendance.
 */
const StatCard = ({ icon: Icon, label, value, trend, color, loading }) => {
  const colorClasses = {
    blue: 'stat-blue',
    green: 'stat-green',
    purple: 'stat-purple',
    orange: 'stat-orange'
  };

  return (
    <div className={`stat-card ${colorClasses[color]}`}>
      <div className="stat-icon">
        <Icon size={24} />
      </div>
      <div className="stat-info">
        <span className="stat-label">{label}</span>
        <span className="stat-value">
          {loading ? '—' : value}
        </span>
        <span className="stat-trend">{trend}</span>
      </div>
    </div>
  );
};

/**
 * Élément de rendez-vous dans la liste.
 */
const RDVItem = ({ rdv }) => {
  const statusConfig = {
    PLANIFIE: { icon: Clock, color: 'status-planned', label: 'Planifié' },
    CONFIRME: { icon: CheckCircle, color: 'status-confirmed', label: 'Confirmé' },
    EN_COURS: { icon: Activity, color: 'status-active', label: 'En cours' },
    TERMINE: { icon: CheckCircle, color: 'status-done', label: 'Terminé' },
    ANNULE: { icon: AlertCircle, color: 'status-cancelled', label: 'Annulé' }
  };

  const status = statusConfig[rdv.statut] || statusConfig.PLANIFIE;
  const StatusIcon = status.icon;

  return (
    <div className="rdv-item">
      <div className="rdv-time">
        <span className="rdv-hour">{rdv.heureDebut}</span>
        <span className="rdv-duration">{rdv.heureFin}</span>
      </div>
      <div className="rdv-info">
        <span className="rdv-patient">
          {rdv.patient ? `${rdv.patient.prenom} ${rdv.patient.nom}` : 'Patient'}
        </span>
        <span className="rdv-motif">{rdv.motif}</span>
      </div>
      <div className={`rdv-status ${status.color}`}>
        <StatusIcon size={14} />
        <span>{status.label}</span>
      </div>
    </div>
  );
};

/**
 * Bouton d'accès rapide.
 */
const QuickAction = ({ icon: Icon, title, description, color, onClick }) => {
  const colorClasses = {
    blue: 'action-blue',
    purple: 'action-purple',
    green: 'action-green',
    orange: 'action-orange'
  };

  return (
    <button 
      className={`quick-action ${colorClasses[color]}`}
      onClick={onClick}
    >
      <div className="action-icon">
        <Icon size={24} />
      </div>
      <div className="action-text">
        <span className="action-title">{title}</span>
        <span className="action-desc">{description}</span>
      </div>
      <ChevronRight size={18} className="action-arrow" />
    </button>
  );
};

export default DashboardMedecin;