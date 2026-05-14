package com.medical.rdvservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO Patient utilisé par le Feign Client.
 * 
 * C'est une COPIE du DTO du Patient Service.
 * Comme les microservices sont indépendants, chaque service
 * définit ses propres DTO pour les objets externes.
 * 
 * Cela évite les dépendances de code entre projets !
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