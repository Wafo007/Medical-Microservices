package com.medical.medecinservice.dto;

import com.medical.medecinservice.entity.Specialite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour transférer les données d'un médecin.
 * 
 * Contient aussi la liste des disponibilités pour éviter
 * d'avoir à faire plusieurs appels API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private Specialite specialite;
    private String telephone;
    private String email;
    private String numeroOrdre;
    
    /**
     * Liste des créneaux de disponibilité intégrée directement.
     * Cela évite au client de faire un appel supplémentaire.
     */
    private List<DisponibiliteDTO> disponibilites;
}