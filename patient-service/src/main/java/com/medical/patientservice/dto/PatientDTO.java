package com.medical.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object
 * ) : objet de transfert de donnees.
 * 
 * Pourquoi un DTO et pas l'entite directement ?
 * 1. Ne pas exposer la structure interne de la base
 * 2. Controler exactement ce qui est envoye/recu
 * 3. Eviter les boucles infinies JSON (relations bidirectionnelles)
 * 4. Valider les donnees entrantes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String telephone;
    private String email;
    private String adresse;
    private String numeroSecu;
}