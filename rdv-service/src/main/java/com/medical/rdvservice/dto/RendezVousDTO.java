package com.medical.rdvservice.dto;

import com.medical.rdvservice.entity.StatutRdv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO pour créer/afficher un rendez-vous.
 * 
 * Contient aussi les objets complets Patient et Medecin
 * pour éviter au frontend de faire plusieurs appels.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousDTO {
    
    private Long id;
    private Long patientId;
    private Long medecinId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String motif;
    private StatutRdv statut;
    private String notes;
    
    // ============================================
    // OBJETS ENRICHS (récupérés via Feign)
    // ============================================
    /**
     * Détails complets du patient (récupérés depuis Patient Service).
     * Null si non chargé (mode léger).
     */
    private PatientDTO patient;
    
    /**
     * Détails complets du médecin (récupérés depuis Medecin Service).
     * Null si non chargé (mode léger).
     */
    private MedecinDTO medecin;
}