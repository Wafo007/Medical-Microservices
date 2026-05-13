package com.medical.patientservice.controller;

import com.medical.patientservice.dto.PatientDTO;
import com.medical.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller : point d'entree HTTP pour les requetes clients.
 * 
 * @RestController = @Controller + @ResponseBody (retourne du JSON auto)
 * @RequestMapping : prefixe de toutes les URLs de ce controller
 * @RequiredArgsConstructor : injection du Service
 * 
 * API RESTful standard :
 * - GET    /api/patients        -> liste
 * - GET    /api/patients/{id}   -> detail
 * - POST   /api/patients        -> creation
 * - PUT    /api/patients/{id}   -> modification
 * - DELETE /api/patients/{id}   -> suppression
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    
    private final PatientService patientService;
    
    // ============================================
    // CREATE
    // ============================================
    
    /**
     * Creer un nouveau patient.
     * @RequestBody : deserialise le JSON recu en objet Java
     * ResponseEntity : controle complet de la reponse HTTP
     */
    @PostMapping
    public ResponseEntity<PatientDTO> creerPatient(@RequestBody PatientDTO patientDTO) {
        PatientDTO cree = patientService.creerPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }
    
    // ============================================
    // READ
    // ============================================
    
    /**
     * Recuperer tous les patients.
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> recupererTousLesPatients() {
        return ResponseEntity.ok(patientService.recupererTousLesPatients());
    }
    
    /**
     * Recuperer un patient par ID.
     * @PathVariable : recupere la valeur de l'URL {id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> recupererPatientParId(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.recupererPatientParId(id));
    }
    
    /**
     * Rechercher des patients par nom.
     * @RequestParam : parametre de requete URL (?nom=Dupont)
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<PatientDTO>> rechercherParNom(
            @RequestParam String nom) {
        return ResponseEntity.ok(patientService.rechercherParNom(nom));
    }
    
    // ============================================
    // UPDATE
    // ============================================
    
    /**
     * Modifier un patient existant.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> modifierPatient(
            @PathVariable Long id,
            @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.modifierPatient(id, patientDTO));
    }
    
    // ============================================
    // DELETE
    // ============================================
    
    /**
     * Supprimer un patient.
     * ResponseEntity.noContent() = HTTP 204 (succes sans contenu)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPatient(@PathVariable Long id) {
        patientService.supprimerPatient(id);
        return ResponseEntity.noContent().build();
    }
}