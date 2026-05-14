package com.medical.rdvservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO Medecin utilisé par le Feign Client.
 * Même principe que PatientDTO : copie locale du format externe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;  // String car on reçoit l'enum en texte
    private String telephone;
    private String email;
    private String numeroOrdre;
    private List<DisponibiliteDTO> disponibilites;
}