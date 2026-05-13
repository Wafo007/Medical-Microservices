package com.medical.medecinservice.service;

import com.medical.medecinservice.dto.DisponibiliteDTO;
import com.medical.medecinservice.dto.MedecinDTO;
import com.medical.medecinservice.entity.Disponibilite;
import com.medical.medecinservice.entity.Medecin;
import com.medical.medecinservice.entity.Specialite;
import com.medical.medecinservice.repository.DisponibiliteRepository;
import com.medical.medecinservice.repository.MedecinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Couche métier du microservice Médecin.
 * 
 * Gère la logique de création, modification, recherche
 * des médecins et de leurs disponibilités.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MedecinService {
    
    private final MedecinRepository medecinRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    
    // ============================================
    // CRUD MEDECIN
    // ============================================
    
    /**
     * Créer un médecin avec ses disponibilités.
     * 
     * @param dto Les données du médecin + dispos
     * @return Le médecin créé avec son ID généré
     */
    public MedecinDTO creerMedecin(MedecinDTO dto) {
        Medecin medecin = new Medecin();
        medecin.setNom(dto.getNom());
        medecin.setPrenom(dto.getPrenom());
        medecin.setSpecialite(dto.getSpecialite());
        medecin.setTelephone(dto.getTelephone());
        medecin.setEmail(dto.getEmail());
        medecin.setNumeroOrdre(dto.getNumeroOrdre());
        
        // Sauvegarde d'abord le médecin pour obtenir son ID
        Medecin sauvegarde = medecinRepository.save(medecin);
        
        // Puis sauvegarde les disponibilités associées
        if (dto.getDisponibilites() != null) {
            List<Disponibilite> dispos = dto.getDisponibilites().stream()
                .map(d -> convertirToDisponibilite(d, sauvegarde))
                .collect(Collectors.toList());
            disponibiliteRepository.saveAll(dispos);
            sauvegarde.setDisponibilites(dispos);
        }
        
        return convertirToDTO(sauvegarde);
    }
    
    @Transactional(readOnly = true)
    public List<MedecinDTO> recupererTousLesMedecins() {
        return medecinRepository.findAll()
                .stream()
                .map(this::convertirToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public MedecinDTO recupererMedecinParId(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Médecin non trouvé avec l'ID : " + id));
        return convertirToDTO(medecin);
    }
    
    public MedecinDTO modifierMedecin(Long id, MedecinDTO dto) {
        Medecin existant = medecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Médecin non trouvé avec l'ID : " + id));
        
        // Mise à jour des champs simples
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setSpecialite(dto.getSpecialite());
        existant.setTelephone(dto.getTelephone());
        existant.setEmail(dto.getEmail());
        existant.setNumeroOrdre(dto.getNumeroOrdre());
        
        // Mise à jour des disponibilités (supprime les anciennes, ajoute les nouvelles)
        existant.getDisponibilites().clear();
        if (dto.getDisponibilites() != null) {
            List<Disponibilite> nouvellesDispos = dto.getDisponibilites().stream()
                .map(d -> convertirToDisponibilite(d, existant))
                .collect(Collectors.toList());
            existant.getDisponibilites().addAll(nouvellesDispos);
        }
        
        return convertirToDTO(medecinRepository.save(existant));
    }
    
    public void supprimerMedecin(Long id) {
        if (!medecinRepository.existsById(id)) {
            throw new RuntimeException("Médecin non trouvé avec l'ID : " + id);
        }
        medecinRepository.deleteById(id);
    }
    
    // ============================================
    // RECHERCHES AVANCEES
    // ============================================
    
    /**
     * Rechercher par spécialité (pour le RDV Service + IA).
     * Ex: "Trouve-moi tous les cardiologues".
     */
    @Transactional(readOnly = true)
    public List<MedecinDTO> rechercherParSpecialite(Specialite specialite) {
        return medecinRepository.findBySpecialite(specialite)
                .stream()
                .map(this::convertirToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MedecinDTO> rechercherParNom(String nom) {
        return medecinRepository.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::convertirToDTO)
                .collect(Collectors.toList());
    }
    
    // ============================================
    // CONVERTISSEURS DTO <-> ENTITY
    // ============================================
    
    private MedecinDTO convertirToDTO(Medecin medecin) {
        List<DisponibiliteDTO> disposDTO = medecin.getDisponibilites().stream()
            .map(this::convertirDisponibiliteToDTO)
            .collect(Collectors.toList());
        
        return new MedecinDTO(
            medecin.getId(),
            medecin.getNom(),
            medecin.getPrenom(),
            medecin.getSpecialite(),
            medecin.getTelephone(),
            medecin.getEmail(),
            medecin.getNumeroOrdre(),
            disposDTO
        );
    }
    
    private DisponibiliteDTO convertirDisponibiliteToDTO(Disponibilite dispo) {
        return new DisponibiliteDTO(
            dispo.getId(),
            dispo.getJour(),
            dispo.getHeureDebut(),
            dispo.getHeureFin(),
            dispo.getSalle()
        );
    }
    
    private Disponibilite convertirToDisponibilite(DisponibiliteDTO dto, Medecin medecin) {
        Disponibilite dispo = new Disponibilite();
        dispo.setJour(dto.getJour());
        dispo.setHeureDebut(dto.getHeureDebut());
        dispo.setHeureFin(dto.getHeureFin());
        dispo.setSalle(dto.getSalle());
        dispo.setMedecin(medecin);  // Lien vers le médecin propriétaire
        return dispo;
    }
}