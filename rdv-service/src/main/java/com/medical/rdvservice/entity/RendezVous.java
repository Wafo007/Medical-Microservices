package com.medical.rdvservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité représentant un rendez-vous médical.
 * 
 * IMPORTANT : Ce service ne stocke PAS les données complètes
 * du patient ni du médecin. Il ne garde que leurs IDs.
 * 
 * Pourquoi ? C'est le principe des microservices :
 * - Patient Service est propriétaire des données patients
 * - Medecin Service est propriétaire des données médecins
 * - RDV Service est propriétaire des données rendez-vous
 * 
 * Chaque service gère SON périmètre. Pas de duplication !
 */
@Entity
@Table(name = "rendez_vous")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVous {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * ID du patient (stocké comme simple Long, pas d'entité JPA).
     * On ira chercher les détails du patient via Feign Client
     * quand on en aura besoin.
     */
    @Column(nullable = false)
    private Long patientId;
    
    /**
     * ID du médecin (même principe).
     */
    @Column(nullable = false)
    private Long medecinId;
    
    /**
     * Date du rendez-vous.
     */
    @Column(nullable = false)
    private LocalDate date;
    
    /**
     * Heure de début du rendez-vous.
     */
    @Column(nullable = false)
    private LocalTime heureDebut;
    
    /**
     * Heure de fin estimée (utile pour éviter les chevauchements).
     */
    @Column(nullable = false)
    private LocalTime heureFin;
    
    /**
     * Motif de la consultation.
     * Ex: "Douleur thoracique", "Renouvellement ordonnance"...
     */
    private String motif;
    
    /**
     * Statut du rendez-vous.
     * Enumération pour garantir des valeurs contrôlées.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRdv statut;
    
    /**
     * Notes libres (résultat de l'IA, observations...).
     */
    @Column(length = 2000)
    private String notes;
}
