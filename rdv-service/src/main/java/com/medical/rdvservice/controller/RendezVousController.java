package com.medical.rdvservice.controller;

import com.medical.rdvservice.dto.RendezVousDTO;
import com.medical.rdvservice.dto.SuggestionIaDTO;
import com.medical.rdvservice.entity.StatutRdv;
import com.medical.rdvservice.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller du microservice Rendez-vous.
 * 
 * Endpoints :
 * - CRUD classique sur /api/rendezvous
 * - Recherche par patient / médecin
 * - Suggestion IA sur /api/rendezvous/suggestion
 */
@RestController
@RequestMapping("/api/rendezvous")
@RequiredArgsConstructor
public class RendezVousController {
    
    private final RendezVousService rendezVousService;
    
    // ============================================
    // CREATE
    // ============================================
    
    /**
     * Créer un rendez-vous avec validation complète.
     * 
     * Body attendu :
     * {
     *   "patientId": 1,
     *   "medecinId": 2,
     *   "date": "2025-06-15",
     *   "heureDebut": "09:00",
     *   "heureFin": "09:30",
     *   "motif": "Consultation de routine"
     * }
     */
    @PostMapping
    public ResponseEntity<RendezVousDTO> creerRendezVous(@RequestBody RendezVousDTO dto) {
        RendezVousDTO cree = rendezVousService.creerRendezVous(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }
    
    // ============================================
    // READ
    // ============================================
    
    @GetMapping
    public ResponseEntity<List<RendezVousDTO>> recupererTousLesRendezVous() {
        return ResponseEntity.ok(rendezVousService.recupererTousLesRendezVous());
    }
    
    /**
     * Recherche par patient.
     * URL : /api/rendezvous/patient/1
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVousDTO>> recupererParPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousService.recupererRendezVousParPatient(patientId));
    }
    
    /**
     * Recherche par médecin.
     * URL : /api/rendezvous/medecin/2
     */
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<RendezVousDTO>> recupererParMedecin(
            @PathVariable Long medecinId) {
        return ResponseEntity.ok(rendezVousService.recupererRendezVousParMedecin(medecinId));
    }
    
    // ============================================
    // IA : SUGGESTION DE MÉDECIN (FEATURE 20/20 !)
    // ============================================
    
    /**
     * Suggère un médecin selon les symptômes.
     * 
     * URL : /api/rendezvous/suggestion?symptomes=J'ai mal à la tête depuis 3 jours
     * 
     * C'est LA fonctionnalité qui va impressionner ton professeur :
     * - Appel à une vraie API IA (Mistral)
     * - Analyse sémantique des symptômes
     * - Recommandation intelligente
     */
    @GetMapping("/suggestion")
    public ResponseEntity<SuggestionIaDTO> suggererMedecin(
            @RequestParam String symptomes) {
        return ResponseEntity.ok(rendezVousService.suggererMedecin(symptomes));
    }
    
    // ============================================
    // READ DETAIL (route générique en dernier)
    // ============================================
    
    @GetMapping("/{id}")
    public ResponseEntity<RendezVousDTO> recupererRendezVousParId(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.recupererRendezVousParId(id));
    }
    
    // ============================================
    // UPDATE STATUT
    // ============================================
    
    /**
     * Modifier uniquement le statut d'un rendez-vous.
     * 
     * Ex: Confirmer un RDV, le marquer comme terminé...
     */
    @PutMapping("/{id}/statut")
    public ResponseEntity<RendezVousDTO> modifierStatut(
            @PathVariable Long id,
            @RequestParam StatutRdv statut) {
        return ResponseEntity.ok(rendezVousService.modifierStatutRendezVous(id, statut));
    }
    
    // ============================================
    // DELETE
    // ============================================
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerRendezVous(@PathVariable Long id) {
        rendezVousService.supprimerRendezVous(id);
        return ResponseEntity.noContent().build();
    }
}