package com.medical.medecinservice.controller;

import com.medical.medecinservice.dto.MedecinDTO;
import com.medical.medecinservice.entity.Specialite;
import com.medical.medecinservice.service.MedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller pour le microservice Médecin.
 * 
 * POINT IMPORTANT : l'ordre des routes compte !
 * Les routes spécifiques (/specialite, /recherche) doivent
 * être déclarées AVANT la route générique /{id}.
 * 
 * Sinon Spring confond "specialite" avec un ID et plante.
 */
@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
public class MedecinController {
    
    private final MedecinService medecinService;
    
    // ============================================
    // CREATE
    // ============================================
    
    @PostMapping
    public ResponseEntity<MedecinDTO> creerMedecin(@RequestBody MedecinDTO medecinDTO) {
        MedecinDTO cree = medecinService.creerMedecin(medecinDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }
    
    // ============================================
    // READ - LISTE
    // ============================================
    
    @GetMapping
    public ResponseEntity<List<MedecinDTO>> recupererTousLesMedecins() {
        return ResponseEntity.ok(medecinService.recupererTousLesMedecins());
    }
    
    // ============================================
    // READ - RECHERCHES (ROUTES SPECIFIQUES AVANT /{id} !)
    // ============================================
    
    /**
     * Rechercher par spécialité.
     * URL : /api/medecins/specialite?specialite=CARDIOLOGIE
     */
    @GetMapping("/specialite")
    public ResponseEntity<List<MedecinDTO>> rechercherParSpecialite(
            @RequestParam Specialite specialite) {
        return ResponseEntity.ok(medecinService.rechercherParSpecialite(specialite));
    }
    
    /**
     * Rechercher par nom.
     * URL : /api/medecins/recherche?nom=Dupont
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<MedecinDTO>> rechercherParNom(
            @RequestParam String nom) {
        return ResponseEntity.ok(medecinService.rechercherParNom(nom));
    }
    
    // ============================================
    // READ - DETAIL (ROUTE GENERIQUE EN DERNIER)
    // ============================================
    
    @GetMapping("/{id}")
    public ResponseEntity<MedecinDTO> recupererMedecinParId(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.recupererMedecinParId(id));
    }
    
    // ============================================
    // UPDATE
    // ============================================
    
    @PutMapping("/{id}")
    public ResponseEntity<MedecinDTO> modifierMedecin(
            @PathVariable Long id,
            @RequestBody MedecinDTO medecinDTO) {
        return ResponseEntity.ok(medecinService.modifierMedecin(id, medecinDTO));
    }
    
    // ============================================
    // DELETE
    // ============================================
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerMedecin(@PathVariable Long id) {
        medecinService.supprimerMedecin(id);
        return ResponseEntity.noContent().build();
    }
}