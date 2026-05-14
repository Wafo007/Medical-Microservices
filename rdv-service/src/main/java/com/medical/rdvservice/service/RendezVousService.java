package com.medical.rdvservice.service;

import com.medical.rdvservice.client.MedecinClient;
import com.medical.rdvservice.client.PatientClient;
import com.medical.rdvservice.dto.*;
import com.medical.rdvservice.entity.RendezVous;
import com.medical.rdvservice.entity.StatutRdv;
import com.medical.rdvservice.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Couche métier du microservice Rendez-vous.
 * 
 * C'est ici que toute la logique intelligente se trouve :
 * - Vérification de l'existence du patient et du médecin
 * - Vérification des disponibilités
 * - Détection des conflits de créneaux
 * - Suggestion IA
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService {
    
    private final RendezVousRepository rendezVousRepository;
    private final PatientClient patientClient;
    private final MedecinClient medecinClient;
    private final MistralService mistralService;
    
    // ============================================
    // CRUD RENDEZ-VOUS
    // ============================================
    
    /**
     * Créer un rendez-vous avec VALIDATION COMPLÈTE.
     * 
     * Étapes de validation :
     * 1. Le patient existe-t-il ? (appel Feign)
     * 2. Le médecin existe-t-il ? (appel Feign)
     * 3. Le médecin est-il disponible ce jour-là ?
     * 4. Y a-t-il un conflit de créneau ?
     * 5. Le patient n'a-t-il pas déjà trop de RDV ce jour ?
     */
    public RendezVousDTO creerRendezVous(RendezVousDTO dto) {
        
        // ---- ÉTAPE 1 : Vérifier que le patient existe ----
        PatientDTO patient;
        try {
            patient = patientClient.getPatientById(dto.getPatientId());
            log.info("Patient trouvé : {} {}", patient.getNom(), patient.getPrenom());
        } catch (Exception e) {
            throw new RuntimeException("Patient introuvable avec l'ID : " + dto.getPatientId());
        }
        
        // ---- ÉTAPE 2 : Vérifier que le médecin existe ----
        MedecinDTO medecin;
        try {
            medecin = medecinClient.getMedecinById(dto.getMedecinId());
            log.info("Médecin trouvé : Dr {} ({})", medecin.getNom(), medecin.getSpecialite());
        } catch (Exception e) {
            throw new RuntimeException("Médecin introuvable avec l'ID : " + dto.getMedecinId());
        }
        
        // ---- ÉTAPE 3 : Vérifier la disponibilité du médecin ----
        verifierDisponibiliteMedecin(medecin, dto.getDate(), dto.getHeureDebut(), dto.getHeureFin());
        
        // ---- ÉTAPE 4 : Vérifier les conflits de créneaux ----
        List<RendezVous> conflits = rendezVousRepository
            .findByMedecinIdAndDateAndHeureDebutLessThanEqualAndHeureFinGreaterThanEqualAndStatutNot(
                dto.getMedecinId(),
                dto.getDate(),
                dto.getHeureFin(),
                dto.getHeureDebut(),
                StatutRdv.ANNULE  // On ignore les RDV annulés
            );
        
        if (!conflits.isEmpty()) {
            throw new RuntimeException(
                "Conflit de créneau ! Le médecin a déjà un rendez-vous " +
                "entre " + conflits.get(0).getHeureDebut() + 
                " et " + conflits.get(0).getHeureFin()
            );
        }
        
        // ---- ÉTAPE 5 : Limiter les RDV par patient/jour (max 3) ----
        long nbRdvJour = rendezVousRepository.countByPatientIdAndDate(
            dto.getPatientId(), dto.getDate()
        );
        if (nbRdvJour >= 3) {
            throw new RuntimeException(
                "Limite atteinte : 3 rendez-vous maximum par jour et par patient."
            );
        }
        
        // ---- CRÉATION ----
        RendezVous rdv = new RendezVous();
        rdv.setPatientId(dto.getPatientId());
        rdv.setMedecinId(dto.getMedecinId());
        rdv.setDate(dto.getDate());
        rdv.setHeureDebut(dto.getHeureDebut());
        rdv.setHeureFin(dto.getHeureFin());
        rdv.setMotif(dto.getMotif());
        rdv.setStatut(StatutRdv.PLANIFIE);
        rdv.setNotes(dto.getNotes());
        
        RendezVous sauvegarde = rendezVousRepository.save(rdv);
        
        // Enrichir le DTO avec les objets complets pour la réponse
        return enrichirDTO(sauvegarde, patient, medecin);
    }
    
    @Transactional(readOnly = true)
    public List<RendezVousDTO> recupererTousLesRendezVous() {
        return rendezVousRepository.findAll().stream()
            .map(rdv -> {
                // Pour chaque RDV, on récupère les détails via Feign
                // En production, on utiliserait un cache pour éviter
                // N appels réseau (pattern Batch ou Cache)
                try {
                    PatientDTO p = patientClient.getPatientById(rdv.getPatientId());
                    MedecinDTO m = medecinClient.getMedecinById(rdv.getMedecinId());
                    return enrichirDTO(rdv, p, m);
                } catch (Exception e) {
                    // Si un service est down, on retourne le RDV sans détails
                    return convertirToDTO(rdv);
                }
            })
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public RendezVousDTO recupererRendezVousParId(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé : " + id));
        
        PatientDTO p = patientClient.getPatientById(rdv.getPatientId());
        MedecinDTO m = medecinClient.getMedecinById(rdv.getMedecinId());
        
        return enrichirDTO(rdv, p, m);
    }
    
    /**
     * Liste les rendez-vous d'un patient spécifique.
     */
    @Transactional(readOnly = true)
    public List<RendezVousDTO> recupererRendezVousParPatient(Long patientId) {
        // Vérifier que le patient existe
        patientClient.getPatientById(patientId);
        
        return rendezVousRepository.findByPatientId(patientId).stream()
            .map(this::convertirToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Liste les rendez-vous d'un médecin spécifique.
     */
    @Transactional(readOnly = true)
    public List<RendezVousDTO> recupererRendezVousParMedecin(Long medecinId) {
        // Vérifier que le médecin existe
        medecinClient.getMedecinById(medecinId);
        
        return rendezVousRepository.findByMedecinId(medecinId).stream()
            .map(this::convertirToDTO)
            .collect(Collectors.toList());
    }
    
    public RendezVousDTO modifierStatutRendezVous(Long id, StatutRdv nouveauStatut) {
        RendezVous rdv = rendezVousRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé : " + id));
        
        rdv.setStatut(nouveauStatut);
        RendezVous misAJour = rendezVousRepository.save(rdv);
        
        PatientDTO p = patientClient.getPatientById(rdv.getPatientId());
        MedecinDTO m = medecinClient.getMedecinById(rdv.getMedecinId());
        
        return enrichirDTO(misAJour, p, m);
    }
    
    public void supprimerRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RuntimeException("Rendez-vous non trouvé : " + id);
        }
        rendezVousRepository.deleteById(id);
    }
    
    // ============================================
    // FONCTIONNALITÉ IA : SUGGESTION DE MÉDECIN
    // ============================================
    
    /**
     * Suggère un médecin selon les symptômes décrits.
     * 
     * Processus :
     * 1. Appelle Mistral AI pour analyser les symptômes
     * 2. Récupère la spécialité suggérée
     * 3. Cherche un médecin de cette spécialité dans notre base
     * 4. Retourne la suggestion enrichie
     */
    @Transactional(readOnly = true)
    public SuggestionIaDTO suggererMedecin(String symptomes) {
        
        // Étape 1 : Analyse IA
        SuggestionIaDTO suggestion = mistralService.analyserSymptomes(symptomes);
        
        // Étape 2 : Chercher un médecin correspondant
        try {
            List<MedecinDTO> medecins = medecinClient.getMedecinsBySpecialite(
                suggestion.getSpecialiteSuggest()
            );
            
            if (!medecins.isEmpty()) {
                MedecinDTO recommande = medecins.get(0);  // Premier disponible
                suggestion.setMedecinIdRecommande(recommande.getId());
                suggestion.setMedecinNomRecommande(
                    "Dr " + recommande.getPrenom() + " " + recommande.getNom()
                );
                suggestion.setExplication(
                    suggestion.getExplication() + 
                    " Nous vous recommandons le Dr " + recommande.getNom() + 
                    " (" + recommande.getSpecialite() + ")."
                );
            } else {
                suggestion.setExplication(
                    suggestion.getExplication() + 
                    " Aucun médecin de cette spécialité n'est disponible dans notre clinique."
                );
            }
        } catch (Exception e) {
            log.error("Impossible de récupérer les médecins", e);
            suggestion.setExplication(
                suggestion.getExplication() + 
                (suggestion.getMedecinNomRecommande() == null ? 
                    " (Service médecin temporairement indisponible)" : "")
            );
        }
        
        return suggestion;
    }
    
    // ============================================
    // MÉTHODES PRIVÉES (logique interne)
    // ============================================
    
    /**
     * Vérifie que le médecin est bien disponible à ce créneau
     * selon ses disponibilités enregistrées.
     */
    private void verifierDisponibiliteMedecin(MedecinDTO medecin, 
                                               LocalDate date, 
                                               LocalTime debut, 
                                               LocalTime fin) {
        
        DayOfWeek jourSemaine = date.getDayOfWeek();
        
        // Cherche si le médecin a une dispo ce jour-là
        boolean dispoTrouvee = medecin.getDisponibilites().stream()
            .anyMatch(d -> 
                d.getJour() == jourSemaine &&
                !debut.isBefore(d.getHeureDebut()) &&
                !fin.isAfter(d.getHeureFin())
            );
        
        if (!dispoTrouvee) {
            throw new RuntimeException(
                "Le Dr " + medecin.getNom() + " n'est pas disponible le " +
                jourSemaine + " entre " + debut + " et " + fin + 
                ". Vérifiez ses créneaux habituels."
            );
        }
    }
    
    /**
     * Convertit une entité en DTO simple (sans objets enrichis).
     */
    private RendezVousDTO convertirToDTO(RendezVous rdv) {
        RendezVousDTO dto = new RendezVousDTO();
        dto.setId(rdv.getId());
        dto.setPatientId(rdv.getPatientId());
        dto.setMedecinId(rdv.getMedecinId());
        dto.setDate(rdv.getDate());
        dto.setHeureDebut(rdv.getHeureDebut());
        dto.setHeureFin(rdv.getHeureFin());
        dto.setMotif(rdv.getMotif());
        dto.setStatut(rdv.getStatut());
        dto.setNotes(rdv.getNotes());
        return dto;
    }
    
    /**
     * Enrichit le DTO avec les objets Patient et Medecin complets.
     */
    private RendezVousDTO enrichirDTO(RendezVous rdv, PatientDTO p, MedecinDTO m) {
        RendezVousDTO dto = convertirToDTO(rdv);
        dto.setPatient(p);
        dto.setMedecin(m);
        return dto;
    }
}