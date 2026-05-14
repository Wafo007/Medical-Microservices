package com.medical.rdvservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la suggestion IA.
 * 
 * Le patient décrit ses symptômes, l'IA suggère une spécialité
 * et éventuellement un médecin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionIaDTO {
    
    /**
     * Description des symptômes par le patient.
     * Ex: "J'ai des douleurs dans la poitrine et je suis essoufflé"
     */
    private String symptomes;
    
    // ============================================
    // CHAMPS REMPLIS PAR L'IA (réponse)
    // ============================================
    
    /**
     * Spécialité suggérée par l'IA.
     * Ex: "CARDIOLOGIE"
     */
    private String specialiteSuggest;
    
    /**
     * Explication de la suggestion.
     * Ex: "Les douleurs thoraciques et l'essoufflement peuvent
     *      indiquer un problème cardiaque. Consultez un cardiologue."
     */
    private String explication;
    
    /**
     * ID du médecin recommandé (si trouvé dans notre base).
     */
    private Long medecinIdRecommande;
    
    /**
     * Nom du médecin recommandé.
     */
    private String medecinNomRecommande;
}