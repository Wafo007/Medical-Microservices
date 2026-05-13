package com.medical.medecinservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Créneau horaire de disponibilité d'un médecin.
 * 
 * Exemple concret :
 * - Médecin : Dr Dupont
 * - Jour : LUNDI
 * - Heure début : 09:00
 * - Heure fin : 12:00
 * 
 * @ManyToOne = plusieurs dispos peuvent appartenir à un même médecin
 */
@Entity
@Table(name = "disponibilites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Jour de la semaine (Java 8 DayOfWeek).
     * Enumération : MONDAY, TUESDAY, WEDNESDAY...
     * Stockée en STRING en base pour lisibilité.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek jour;
    
    /**
     * Heure de début du créneau.
     * LocalTime = meilleur choix que Date pour une heure simple.
     * Format en base : '09:00:00'
     */
    @Column(nullable = false)
    private LocalTime heureDebut;
    
    @Column(nullable = false)
    private LocalTime heureFin;
    
    /**
     * Salle ou cabinet où consulte le médecin ce jour-là.
     */
    private String salle;
    
    /**
     * Clé étrangère vers le médecin.
     * 
     * @JoinColumn(name = "medecin_id") → colonne SQL medecin_id
     * nullable = false → une dispo DOIT avoir un médecin
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;
}