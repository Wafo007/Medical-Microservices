package com.medical.rdvservice.client;

import com.medical.rdvservice.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour communiquer avec le Patient Service.
 * 
 * @FeignClient(name = "PATIENT-SERVICE") :
 * - 'name' = nom du service dans Eureka
 * - Feign demande à Eureka l'IP/port de PATIENT-SERVICE
 * - Puis fait automatiquement l'appel HTTP !
 * 
 * C'est comme écrire :
 * RestTemplate.getForObject("http://PATIENT-SERVICE/api/patients/1", PatientDTO.class)
 * Mais en BEAUCOUP plus propre et déclaratif.
 */
@FeignClient(name = "PATIENT-SERVICE")
public interface PatientClient {
    
    /**
     * Récupère un patient par son ID.
     * 
     * L'URL correspond exactement au @GetMapping du PatientController.
     */
    @GetMapping("/api/patients/{id}")
    PatientDTO getPatientById(@PathVariable("id") Long id);
}