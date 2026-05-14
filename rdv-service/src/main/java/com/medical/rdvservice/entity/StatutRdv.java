package com.medical.rdvservice.entity;

/**
 * Enumération des statuts possibles pour un rendez-vous médical.
 * 
 * Pourquoi un enum et pas des String ?
 * - Sécurité : impossible de mettre une valeur invalide en base
 * - Lisibilité : le code parle de lui-même (PLANIFIE vs "planifie")
 * - Maintenance : un seul endroit pour modifier/ajouter des statuts
 * 
 * @Enumerated(EnumType.STRING) dans l'entité RendezVous garantit
 * que c'est le NOM (ex: "PLANIFIE") qui est stocké en base,
 * pas un nombre (0, 1, 2...) qui serait incompréhensible.
 */
public enum StatutRdv {
    
    /**
     * Rendez-vous créé mais pas encore confirmé par le médecin.
     */
    PLANIFIE,
    
    /**
     * Rendez-vous confirmé par les deux parties.
     * Le patient et le médecin sont informés.
     */
    CONFIRME,
    
    /**
     * Le patient est actuellement en consultation.
     * Statut temporaire pendant la visite.
     */
    EN_COURS,
    
    /**
     * La consultation est terminée.
     * Le médecin a clos le rendez-vous.
     */
    TERMINE,
    
    /**
     * Rendez-vous annulé par le patient ou le médecin.
     * Les créneaux annulés ne bloquent plus le planning.
     */
    ANNULE,
    
    /**
     * Le patient ne s'est pas présenté.
     * Distinction importante pour les statistiques.
     */
    NO_SHOW
}