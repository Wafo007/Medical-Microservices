import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  Calendar, ChevronRight, Clock, User, Stethoscope,
  CheckCircle, AlertCircle, Loader2, ArrowLeft
} from 'lucide-react';
import { medecinAPI, rdvAPI, patientAPI } from '../services/api';
import Sidebar from '../components/Sidebar';
import './PrendreRDVPage.css';

/**
 * Page Prendre RDV — Formulaire de création de rendez-vous.
 * 
 * Processus métier :
 * 1. L'utilisateur sélectionne un patient
 * 2. Il choisit un médecin (liste depuis Medecin Service)
 * 3. Il sélectionne une date
 * 4. Le système vérifie les disponibilités du médecin
 * 5. Il choisit un créneau horaire
 * 6. Validation et création du RDV
 * 
 * Communication inter-services :
 * Frontend → Gateway → [Patient Service, Medecin Service, RDV Service]
 */

const PrendreRDVPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const username = localStorage.getItem('username') || 'Médecin';
  const role = localStorage.getItem('role') || 'MEDECIN';
  
  // Données pré-remplies depuis la navigation (ex: depuis Assistant IA)
  const preselectedMedecin = location.state?.medecinId || null;
  const preselectedSpecialite = location.state?.specialite || null;

  // Étapes du wizard (formulaire multi-étapes)
  const [step, setStep] = useState(1);
  
  // Données du formulaire
  const [formData, setFormData] = useState({
    patientId: '',
    medecinId: preselectedMedecin || '',
    date: '',
    heureDebut: '',
    heureFin: '',
    motif: ''
  });
  
  // Données chargées depuis les APIs
  const [patients, setPatients] = useState([]);
  const [medecins, setMedecins] = useState([]);
  const [disponibilites, setDisponibilites] = useState([]);
  const [creneaux, setCreneaux] = useState([]);
  
  // État UI
  const [loading, setLoading] = useState(false);
  const [loadingCreneaux, setLoadingCreneaux] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [createdRDV, setCreatedRDV] = useState(null);

  // Menu sidebar
  const menuItems = role === 'MEDECIN'
    ? [
        { icon: User, label: 'Dashboard', path: '/dashboard-medecin' },
        { icon: User, label: 'Patients', path: '/patients' },
        { icon: User, label: 'Rendez-vous', path: '/rendezvous' },
        { icon: User, label: 'Assistant IA', path: '/assistant-ia' },
      ]
    : [
        { icon: User, label: 'Dashboard', path: '/dashboard-patient' },
        { icon: User, label: 'Prendre RDV', active: true, path: '/prendre-rdv' },
        { icon: User, label: 'Assistant IA', path: '/assistant-ia' },
      ];

  // Chargement initial des données
  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      
      // Chargement parallèle des patients et médecins
      const [patientsRes, medecinsRes] = await Promise.all([
        patientAPI.getAll(),
        medecinAPI.getAll()
      ]);
      
      setPatients(patientsRes.data);
      setMedecins(medecinsRes.data);
      
      // Si un médecin était pré-sélectionné (depuis l'IA), on charge ses dispos
      if (preselectedMedecin) {
        const medecin = medecinsRes.data.find(m => m.id === preselectedMedecin);
        if (medecin) {
          setDisponibilites(medecin.disponibilites || []);
        }
      }
      
    } catch (err) {
      setError('Impossible de charger les données nécessaires.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Quand un médecin est sélectionné, charge ses disponibilités
   */
  const handleMedecinChange = (medecinId) => {
    const medecin = medecins.find(m => m.id === parseInt(medecinId));
    setDisponibilites(medecin?.disponibilites || []);
    setFormData({ ...formData, medecinId, date: '', heureDebut: '', heureFin: '' });
    setCreneaux([]);
  };

  /**
   * Quand une date est sélectionnée, génère les créneaux disponibles
   */
  const handleDateChange = (date) => {
    if (!date || !formData.medecinId) return;
    
    setFormData({ ...formData, date, heureDebut: '', heureFin: '' });
    setLoadingCreneaux(true);
    
    // Trouve le jour de la semaine (0=Dimanche, 1=Lundi...)
    const jourSemaine = new Date(date).getDay();
    const joursMap = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    const jourNom = joursMap[jourSemaine];
    
    // Filtre les disponibilités du médecin pour ce jour
    const disposJour = disponibilites.filter(d => d.jour === jourNom);
    
    // Génère des créneaux de 30 minutes
    const creneauxGeneres = [];
    disposJour.forEach(dispo => {
      let current = parseTime(dispo.heureDebut);
      const fin = parseTime(dispo.heureFin);
      
      while (current < fin) {
        const debutStr = formatTime(current);
        const finCreneau = addMinutes(current, 30);
        const finStr = formatTime(finCreneau);
        
        creneauxGeneres.push({
          debut: debutStr,
          fin: finStr,
          salle: dispo.salle
        });
        
        current = finCreneau;
      }
    });
    
    setCreneaux(creneauxGeneres);
    setLoadingCreneaux(false);
  };

  /**
   * Soumission finale du formulaire
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const response = await rdvAPI.create(formData);
      setCreatedRDV(response.data);
      setSuccess(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création du rendez-vous.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Passe à l'étape suivante
   */
  const nextStep = () => {
    setError('');
    setStep(step + 1);
  };

  /**
   * Retourne à l'étape précédente
   */
  const prevStep = () => {
    setError('');
    setStep(step - 1);
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  // ============================================
  // RENDU DES ÉTAPES
  // ============================================

  const renderStep = () => {
    switch (step) {
      case 1:
        return <StepPatient />;
      case 2:
        return <StepMedecin />;
      case 3:
        return <StepDateCreneau />;
      case 4:
        return <StepConfirmation />;
      default:
        return null;
    }
  };

  // Étape 1 : Sélection du patient
  const StepPatient = () => (
    <div className="step-content">
      <h2>Étape 1 : Sélectionner un patient</h2>
      <p className="step-desc">Choisissez le patient pour lequel vous souhaitez prendre un rendez-vous.</p>
      
      <div className="patients-grid">
        {patients.map(patient => (
          <button
            key={patient.id}
            className={`patient-card ${formData.patientId === patient.id ? 'selected' : ''}`}
            onClick={() => setFormData({ ...formData, patientId: patient.id })}
          >
            <div className="patient-avatar">
              {patient.prenom.charAt(0)}{patient.nom.charAt(0)}
            </div>
            <div className="patient-info">
              <span className="patient-name">{patient.prenom} {patient.nom}</span>
              <span className="patient-details">Né le {formatDate(patient.dateNaissance)}</span>
              <span className="patient-secu">{patient.numeroSecu}</span>
            </div>
            {formData.patientId === patient.id && (
              <CheckCircle className="selected-icon" size={24} />
            )}
          </button>
        ))}
      </div>
      
      <div className="step-actions">
        <button 
          className="btn btn-primary"
          onClick={nextStep}
          disabled={!formData.patientId}
        >
          Suivant <ChevronRight size={18} />
        </button>
      </div>
    </div>
  );

  // Étape 2 : Sélection du médecin
  const StepMedecin = () => (
    <div className="step-content">
      <h2>Étape 2 : Choisir un médecin</h2>
      <p className="step-desc">Sélectionnez le spécialiste souhaité.</p>
      
      {preselectedSpecialite && (
        <div className="info-banner">
          <Stethoscope size={18} />
          Suggestion IA : spécialité {preselectedSpecialite}
        </div>
      )}
      
      <div className="medecins-grid">
        {medecins.map(medecin => (
          <button
            key={medecin.id}
            className={`medecin-card ${formData.medecinId === medecin.id ? 'selected' : ''}`}
            onClick={() => handleMedecinChange(medecin.id)}
          >
            <div className="medecin-avatar">
              <Stethoscope size={24} />
            </div>
            <div className="medecin-info">
              <span className="medecin-name">Dr. {medecin.prenom} {medecin.nom}</span>
              <span className="medecin-specialite">{medecin.specialite}</span>
              <span className="medecin-ordre">N° {medecin.numeroOrdre}</span>
            </div>
            {formData.medecinId === medecin.id && (
              <CheckCircle className="selected-icon" size={24} />
            )}
          </button>
        ))}
      </div>
      
      <div className="step-actions">
        <button className="btn btn-outline" onClick={prevStep}>
          <ArrowLeft size={18} /> Retour
        </button>
        <button 
          className="btn btn-primary"
          onClick={nextStep}
          disabled={!formData.medecinId}
        >
          Suivant <ChevronRight size={18} />
        </button>
      </div>
    </div>
  );

  // Étape 3 : Date et créneau
  const StepDateCreneau = () => (
    <div className="step-content">
      <h2>Étape 3 : Choisir la date et l'heure</h2>
      <p className="step-desc">Sélectionnez une date puis un créneau horaire disponible.</p>
      
      <div className="form-group date-group">
        <label>Date du rendez-vous</label>
        <input
          type="date"
          value={formData.date}
          onChange={(e) => handleDateChange(e.target.value)}
          min={new Date().toISOString().split('T')[0]}
        />
      </div>
      
      {formData.date && (
        <div className="creneaux-section">
          <h3>Créneaux disponibles</h3>
          {loadingCreneaux ? (
            <div className="loading-creneaux">
              <Loader2 size={24} className="spin" />
              Chargement des disponibilités...
            </div>
          ) : creneaux.length === 0 ? (
            <div className="no-creneaux">
              <AlertCircle size={24} />
              Aucun créneau disponible pour cette date. Le médecin ne consulte pas ce jour-là.
            </div>
          ) : (
            <div className="creneaux-grid">
              {creneaux.map((creneau, idx) => (
                <button
                  key={idx}
                  className={`creneau-btn ${formData.heureDebut === creneau.debut ? 'selected' : ''}`}
                  onClick={() => setFormData({ 
                    ...formData, 
                    heureDebut: creneau.debut,
                    heureFin: creneau.fin
                  })}
                >
                  <Clock size={16} />
                  <span>{creneau.debut} - {creneau.fin}</span>
                  <span className="creneau-salle">{creneau.salle}</span>
                </button>
              ))}
            </div>
          )}
        </div>
      )}
      
      <div className="form-group motif-group">
        <label>Motif de la consultation</label>
        <input
          type="text"
          value={formData.motif}
          onChange={(e) => setFormData({ ...formData, motif: e.target.value })}
          placeholder="Ex: Consultation de routine, douleur thoracique..."
        />
      </div>
      
      <div className="step-actions">
        <button className="btn btn-outline" onClick={prevStep}>
          <ArrowLeft size={18} /> Retour
        </button>
        <button 
          className="btn btn-primary"
          onClick={nextStep}
          disabled={!formData.date || !formData.heureDebut || !formData.motif}
        >
          Suivant <ChevronRight size={18} />
        </button>
      </div>
    </div>
  );

  // Étape 4 : Confirmation et création
  const StepConfirmation = () => {
    const patient = patients.find(p => p.id === parseInt(formData.patientId));
    const medecin = medecins.find(m => m.id === parseInt(formData.medecinId));
    
    return (
      <div className="step-content">
        <h2>Étape 4 : Confirmation</h2>
        <p className="step-desc">Vérifiez les informations avant de confirmer le rendez-vous.</p>
        
        <div className="recap-card">
          <div className="recap-row">
            <User size={18} />
            <div>
              <span className="recap-label">Patient</span>
              <span className="recap-value">{patient?.prenom} {patient?.nom}</span>
            </div>
          </div>
          
          <div className="recap-row">
            <Stethoscope size={18} />
            <div>
              <span className="recap-label">Médecin</span>
              <span className="recap-value">Dr. {medecin?.prenom} {medecin?.nom} ({medecin?.specialite})</span>
            </div>
          </div>
          
          <div className="recap-row">
            <Calendar size={18} />
            <div>
              <span className="recap-label">Date</span>
              <span className="recap-value">{formatDate(formData.date)}</span>
            </div>
          </div>
          
          <div className="recap-row">
            <Clock size={18} />
            <div>
              <span className="recap-label">Horaire</span>
              <span className="recap-value">{formData.heureDebut} - {formData.heureFin}</span>
            </div>
          </div>
          
          <div className="recap-row">
            <AlertCircle size={18} />
            <div>
              <span className="recap-label">Motif</span>
              <span className="recap-value">{formData.motif}</span>
            </div>
          </div>
        </div>
        
        <div className="step-actions">
          <button className="btn btn-outline" onClick={prevStep}>
            <ArrowLeft size={18} /> Modifier
          </button>
          <button 
            className="btn btn-primary btn-large"
            onClick={handleSubmit}
            disabled={loading}
          >
            {loading ? (
              <><Loader2 size={18} className="spin" /> Création...</>
            ) : (
              <><CheckCircle size={18} /> Confirmer le rendez-vous</>
            )}
          </button>
        </div>
      </div>
    );
  };

  // ============================================
  // RENDU PRINCIPAL
  // ============================================

  if (success && createdRDV) {
    return (
      <div className="dashboard">
        <Sidebar menuItems={menuItems} userName={username} userRole={role} onLogout={handleLogout} />
        <main className="dashboard-main success-page">
          <div className="success-card">
            <div className="success-icon">
              <CheckCircle size={64} />
            </div>
            <h2>Rendez-vous créé avec succès !</h2>
            <p>Le rendez-vous a été enregistré dans le système.</p>
            
            <div className="success-details">
              <p><strong>Patient :</strong> {createdRDV.patient?.prenom} {createdRDV.patient?.nom}</p>
              <p><strong>Médecin :</strong> Dr. {createdRDV.medecin?.prenom} {createdRDV.medecin?.nom}</p>
              <p><strong>Date :</strong> {formatDate(createdRDV.date)} à {createdRDV.heureDebut}</p>
            </div>
            
            <div className="success-actions">
              <button className="btn btn-outline" onClick={() => navigate('/rendezvous')}>
                Voir les rendez-vous
              </button>
              <button className="btn btn-primary" onClick={() => {
                setSuccess(false);
                setStep(1);
                setFormData({
                  patientId: '',
                  medecinId: '',
                  date: '',
                  heureDebut: '',
                  heureFin: '',
                  motif: ''
                });
              }}>
                Nouveau rendez-vous
              </button>
            </div>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <Sidebar menuItems={menuItems} userName={username} userRole={role} onLogout={handleLogout} />
      
      <main className="dashboard-main">
        <header className="page-header">
          <div>
            <h1>
              <Calendar size={28} />
              Prendre un Rendez-vous
            </h1>
            <p className="breadcrumb">
              Clinic+ <ChevronRight size={14} /> Rendez-vous <ChevronRight size={14} /> Nouveau
            </p>
          </div>
        </header>

        {error && (
          <div className="alert alert-error">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        {/* Indicateur de progression */}
        <div className="progress-bar">
          {[1, 2, 3, 4].map(s => (
            <div key={s} className={`progress-step ${s === step ? 'active' : s < step ? 'completed' : ''}`}>
              <span className="step-number">{s < step ? '✓' : s}</span>
              <span className="step-label">
                {s === 1 && 'Patient'}
                {s === 2 && 'Médecin'}
                {s === 3 && 'Date/Heure'}
                {s === 4 && 'Confirmation'}
              </span>
            </div>
          ))}
        </div>

        {loading && !formData.patientId ? (
          <div className="loading-state">
            <Loader2 size={32} className="spin" />
            <p>Chargement des données...</p>
          </div>
        ) : (
          renderStep()
        )}
      </main>
    </div>
  );
};

// ============================================
// UTILITAIRES
// ============================================

const parseTime = (timeStr) => {
  const [hours, minutes] = timeStr.split(':').map(Number);
  return hours * 60 + minutes;
};

const formatTime = (minutes) => {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`;
};

const addMinutes = (time, mins) => time + mins;

const formatDate = (isoDate) => {
  if (!isoDate) return '—';
  return new Date(isoDate).toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric'
  });
};

export default PrendreRDVPage;