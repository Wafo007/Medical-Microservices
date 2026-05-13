package com.medical.medecinservice.entity;

/**
 * Énumération des spécialités médicales.
 * 
 * Utilisée comme type de données pour garantir
 * que seules des valeurs valides sont insérées en base.
 * 
 * Avantage : pas de fautes de frappe possibles !
 * (ex: "cardiologie" vs "cardiologgie" vs "Cardiologie")
 */
public enum Specialite {
    CARDIOLOGIE,      // Cœur et vaisseaux sanguins
    DERMATOLOGIE,     // Peau
    PEDIATRIE,        // Enfants
    NEUROLOGIE,       // Cerveau et système nerveux
    OPHTALMOLOGIE,    // Yeux
    ORL,              // Oreille-Nez-Gorge
    GENERALISTE,      // Médecine générale
    GYNECOLOGIE,      // Santé féminine
    ORTHOPEDIE,       // Os et articulations
    PSYCHIATRIE       // Santé mentale
}