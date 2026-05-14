package com.medical.rdvservice.client;

import com.medical.rdvservice.dto.MedecinDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Client Feign pour communiquer avec le Medecin Service.
 */
@FeignClient(name = "MEDECIN-SERVICE")
public interface MedecinClient {
    
    /**
     * Récupère un médecin par son ID.
     */
    @GetMapping("/api/medecins/{id}")
    MedecinDTO getMedecinById(@PathVariable("id") Long id);
    
    /**
     * Recherche les médecins par spécialité.
     * Utilisé par l'IA pour trouver un médecin adapté.
     */
    @GetMapping("/api/medecins/specialite")
    List<MedecinDTO> getMedecinsBySpecialite(@RequestParam("specialite") String specialite);
}