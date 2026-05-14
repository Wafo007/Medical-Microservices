package com.medical.rdvservice.repository;

import com.medical.rdvservice.entity.RendezVous;
import com.medical.rdvservice.entity.StatutRdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository pour les rendez-vous.
 * 
 * Requêtes personnalisées pour la logique métier :
 * - Vérifier les conflits de créneaux
 * - Lister les RDV d'un patient ou d'un médecin
 */
@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    
    /**
     * Tous les rendez-vous d'un patient.
     */
    List<RendezVous> findByPatientId(Long patientId);
    
    /**
     * Tous les rendez-vous d'un médecin.
     */
    List<RendezVous> findByMedecinId(Long medecinId);
    
    /**
     * Rendez-vous d'un médecin à une date précise.
     * Utile pour afficher son planning journalier.
     */
    List<RendezVous> findByMedecinIdAndDate(Long medecinId, LocalDate date);
    
    /**
     * VÉRIFICATION DE CONFLIT (requête la plus importante).
     * 
     * Cherche s'il existe déjà un RDV pour ce médecin, ce jour,
     * avec un chevauchement horaire.
     * 
     * Un chevauchement existe si :
     * - Le nouveau RDV commence avant la fin d'un existant
     *   ET finit après le début d'un existant
     * 
     * Cette requête empêche de planifier 2 RDV au même moment !
     */
    List<RendezVous> findByMedecinIdAndDateAndHeureDebutLessThanEqualAndHeureFinGreaterThanEqualAndStatutNot(
            Long medecinId,
            LocalDate date,
            LocalTime heureFin,      // Nouveau RDV finit après...
            LocalTime heureDebut,    // ...le début d'un existant
            StatutRdv statut         // Ignore les RDV annulés
    );
    
    /**
     * Compte les rendez-vous d'un patient à une date donnée.
     * Pour éviter qu'un patient prenne 10 RDV le même jour.
     */
    long countByPatientIdAndDate(Long patientId, LocalDate date);
}