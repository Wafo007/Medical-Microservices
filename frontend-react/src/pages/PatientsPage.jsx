import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Users, Search, Plus, Edit2, Trash2, X, Check, ChevronRight,
  AlertCircle, Loader2, UserPlus
} from 'lucide-react';
import { patientAPI } from '../services/api';
import Sidebar from '../components/Sidebar';
import './PatientsPage.css';

/**
 * Page Liste Patients — CRUD complet.
 * 
 * Fonctionnalités :
 * - Affichage de tous les patients (tableau)
 * - Recherche par nom
 * - Création d'un patient (modal)
 * - Modification d'un patient (modal)
 * - Suppression d'un patient (confirmation)
 * 
 * Communication : Frontend → Gateway → Patient Service → PostgreSQL
 */

const PatientsPage = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('username') || 'Médecin';
  
  // État des données
  const [patients, setPatients] = useState([]);
  const [filteredPatients, setFilteredPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  
  // État des modals
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState('create'); // 'create' ou 'edit'
  const [selectedPatient, setSelectedPatient] = useState(null);
  
  // État du formulaire
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    dateNaissance: '',
    telephone: '',
    email: '',
    adresse: '',
    numeroSecu: ''
  });

  // Menu sidebar
  const menuItems = [
    { icon: Users, label: 'Dashboard', path: '/dashboard-medecin' },
    { icon: Users, label: 'Patients', active: true, path: '/patients' },
    { icon: Users, label: 'Médecins', path: '/medecins' },
    { icon: Users, label: 'Rendez-vous', path: '/rendezvous' },
    { icon: Users, label: 'Assistant IA', path: '/assistant-ia' },
  ];

  // Chargement initial
  useEffect(() => {
    fetchPatients();
  }, []);

  // Filtrage lors de la recherche
  useEffect(() => {
    if (searchTerm.trim() === '') {
      setFilteredPatients(patients);
    } else {
      const filtered = patients.filter(p => 
        p.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.prenom.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.numeroSecu.includes(searchTerm)
      );
      setFilteredPatients(filtered);
    }
  }, [searchTerm, patients]);

  /**
   * Récupère tous les patients depuis l'API
   */
  const fetchPatients = async () => {
    try {
      setLoading(true);
      const response = await patientAPI.getAll();
      setPatients(response.data);
      setFilteredPatients(response.data);
      setError('');
    } catch (err) {
      setError('Impossible de charger les patients. Vérifiez que le service est disponible.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Ouvre le modal de création
   */
  const handleCreate = () => {
    setModalMode('create');
    setFormData({
      nom: '',
      prenom: '',
      dateNaissance: '',
      telephone: '',
      email: '',
      adresse: '',
      numeroSecu: ''
    });
    setShowModal(true);
  };

  /**
   * Ouvre le modal d'édition
   */
  const handleEdit = (patient) => {
    setModalMode('edit');
    setSelectedPatient(patient);
    setFormData({
      nom: patient.nom,
      prenom: patient.prenom,
      dateNaissance: patient.dateNaissance,
      telephone: patient.telephone || '',
      email: patient.email || '',
      adresse: patient.adresse || '',
      numeroSecu: patient.numeroSecu
    });
    setShowModal(true);
  };

  /**
   * Supprime un patient après confirmation
   */
  const handleDelete = async (id) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce patient ? Cette action est irréversible.')) {
      return;
    }

    try {
      await patientAPI.delete(id);
      // Met à jour la liste localement
      setPatients(patients.filter(p => p.id !== id));
      setError('');
    } catch (err) {
      setError('Erreur lors de la suppression du patient.');
    }
  };

  /**
   * Soumet le formulaire (création ou modification)
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      if (modalMode === 'create') {
        const response = await patientAPI.create(formData);
        setPatients([...patients, response.data]);
      } else {
        const response = await patientAPI.update(selectedPatient.id, formData);
        setPatients(patients.map(p => 
          p.id === selectedPatient.id ? response.data : p
        ));
      }
      
      setShowModal(false);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement.');
    }
  };

  /**
   * Gestion des changements dans le formulaire
   */
  const handleFormChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <div className="dashboard">
      <Sidebar 
        menuItems={menuItems}
        userName={username}
        userRole="MEDECIN"
        onLogout={handleLogout}
      />

      <main className="dashboard-main">
        {/* Header */}
        <header className="page-header">
          <div>
            <h1>
              <Users size={28} />
              Gestion des Patients
            </h1>
            <p className="breadcrumb">
              Clinic+ <ChevronRight size={14} /> Patients
            </p>
          </div>
          <button className="btn btn-primary" onClick={handleCreate}>
            <Plus size={18} />
            Nouveau Patient
          </button>
        </header>

        {/* Message d'erreur */}
        {error && (
          <div className="alert alert-error">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        {/* Barre de recherche */}
        <div className="search-bar">
          <Search size={20} className="search-icon" />
          <input
            type="text"
            placeholder="Rechercher par nom, prénom ou numéro de sécurité sociale..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          {searchTerm && (
            <button className="search-clear" onClick={() => setSearchTerm('')}>
              <X size={16} />
            </button>
          )}
        </div>

        {/* Tableau des patients */}
        <div className="table-container">
          {loading ? (
            <div className="loading-state">
              <Loader2 size={32} className="spin" />
              <p>Chargement des patients...</p>
            </div>
          ) : filteredPatients.length === 0 ? (
            <div className="empty-state">
              <UserPlus size={48} className="empty-icon" />
              <h3>Aucun patient trouvé</h3>
              <p>{searchTerm ? 'Essayez une autre recherche' : 'Commencez par ajouter un patient'}</p>
              {!searchTerm && (
                <button className="btn btn-primary" onClick={handleCreate}>
                  <Plus size={18} />
                  Ajouter un patient
                </button>
              )}
            </div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nom</th>
                  <th>Prénom</th>
                  <th>Date de naissance</th>
                  <th>Téléphone</th>
                  <th>N° Sécu</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredPatients.map((patient) => (
                  <tr key={patient.id}>
                    <td className="cell-id">#{patient.id}</td>
                    <td className="cell-name">{patient.nom}</td>
                    <td>{patient.prenom}</td>
                    <td>{formatDate(patient.dateNaissance)}</td>
                    <td>{patient.telephone || '—'}</td>
                    <td className="cell-secu">{patient.numeroSecu}</td>
                    <td className="cell-actions">
                      <button 
                        className="btn-icon btn-edit"
                        onClick={() => handleEdit(patient)}
                        title="Modifier"
                      >
                        <Edit2 size={16} />
                      </button>
                      <button 
                        className="btn-icon btn-delete"
                        onClick={() => handleDelete(patient.id)}
                        title="Supprimer"
                      >
                        <Trash2 size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* Compteur de résultats */}
        {!loading && filteredPatients.length > 0 && (
          <div className="results-count">
            {filteredPatients.length} patient{filteredPatients.length > 1 ? 's' : ''} trouvé{filteredPatients.length > 1 ? 's' : ''}
            {searchTerm && ` sur ${patients.length} total`}
          </div>
        )}
      </main>

      {/* Modal Création/Édition */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>
                {modalMode === 'create' ? (
                  <><UserPlus size={24} /> Nouveau Patient</>
                ) : (
                  <><Edit2 size={24} /> Modifier Patient</>
                )}
              </h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>
                <X size={24} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="modal-form">
              <div className="form-row">
                <div className="form-group">
                  <label>Nom *</label>
                  <input
                    type="text"
                    name="nom"
                    value={formData.nom}
                    onChange={handleFormChange}
                    required
                    placeholder="Dupont"
                  />
                </div>
                <div className="form-group">
                  <label>Prénom *</label>
                  <input
                    type="text"
                    name="prenom"
                    value={formData.prenom}
                    onChange={handleFormChange}
                    required
                    placeholder="Jean"
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Date de naissance *</label>
                  <input
                    type="date"
                    name="dateNaissance"
                    value={formData.dateNaissance}
                    onChange={handleFormChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Téléphone</label>
                  <input
                    type="tel"
                    name="telephone"
                    value={formData.telephone}
                    onChange={handleFormChange}
                    placeholder="0612345678"
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleFormChange}
                  placeholder="jean.dupont@email.com"
                />
              </div>

              <div className="form-group">
                <label>Adresse</label>
                <input
                  type="text"
                  name="adresse"
                  value={formData.adresse}
                  onChange={handleFormChange}
                  placeholder="12 Rue de Paris, 75001 Paris"
                />
              </div>

              <div className="form-group">
                <label>N° Sécurité Sociale *</label>
                <input
                  type="text"
                  name="numeroSecu"
                  value={formData.numeroSecu}
                  onChange={handleFormChange}
                  required
                  placeholder="185031512345678"
                  maxLength={15}
                />
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-outline" onClick={() => setShowModal(false)}>
                  <X size={18} />
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary">
                  <Check size={18} />
                  {modalMode === 'create' ? 'Créer' : 'Enregistrer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

// ============================================
// UTILITAIRES
// ============================================

/**
 * Formate une date ISO en format français
 */
const formatDate = (isoDate) => {
  if (!isoDate) return '—';
  const date = new Date(isoDate);
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
};

export default PatientsPage;