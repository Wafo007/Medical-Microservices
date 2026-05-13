package com.medical.patientservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Antecedent medical d'un patient (allergies, maladies, operations...).
 * 
 * Relation @ManyToOne : plusieurs antecedents peuvent appartenir 
 * a un seul patient.
 */
@Entity
@Table(name = "antecedents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Antecedent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;  // ex: "Allergie", "Maladie chronique", "Operation"
    
    @Column(nullable = false)
    private String description;
    
    private LocalDate dateDiagnostic;
    
    private String gravite;  // "Legere", "Moderee", "Grave"
    
    /**
     * @JoinColumn : colonne de jointure vers la table patients.
     * patient_id = cle etrangere en base de donnees.
     */
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY = charge le patient seulement si besoin
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
}