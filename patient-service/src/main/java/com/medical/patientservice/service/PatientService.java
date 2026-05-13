package com.medical.patientservice.service;

import com.medical.patientservice.dto.PatientDTO;
import com.medical.patientservice.entity.Patient;
import com.medical.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Couche metier (Business Logic).
 * 
 * @Service : declare ce bean comme service Spring
 * @RequiredArgsConstructor : Lombok genere un constructeur avec 
 *                            tous les 'final' (injection de dependances)
 * @Transactional : garantit l'atomicite des operations base de donnees
 */
@Service
@RequiredArgsConstructor  // Injection automatique du Repository
@Transactional            // Toute methode est transactionnelle par defaut
public class PatientService {
    
    /**
     * Repository injecte par le constructeur (pattern recommande).
     * 'final' = obligatoire, ne peut pas etre null.
     */
    private final PatientRepository patientRepository;
    
    // ============================================
    // OPERATIONS CRUD
    // ============================================
    
    /**
     * CREATE : creer un nouveau patient.
     */
    public PatientDTO creerPatient(PatientDTO dto) {
        Patient patient = convertirToEntity(dto);
        Patient sauvegarde = patientRepository.save(patient);
        return convertirToDTO(sauvegarde);
    }
    
    /**
     * READ : recuperer tous les patients.
     */
    @Transactional(readOnly = true)  // Optimisation : pas d'ecriture
    public List<PatientDTO> recupererTousLesPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::convertirToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * READ : recuperer un patient par son ID.
     */
    @Transactional(readOnly = true)
    public PatientDTO recupererPatientParId(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Patient non trouve avec l'ID : " + id));
        return convertirToDTO(patient);
    }
    
    /**
     * UPDATE : modifier un patient existant.
     */
    public PatientDTO modifierPatient(Long id, PatientDTO dto) {
        Patient existant = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Patient non trouve avec l'ID : " + id));
        
        // Mise a jour des champs
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setDateNaissance(dto.getDateNaissance());
        existant.setTelephone(dto.getTelephone());
        existant.setEmail(dto.getEmail());
        existant.setAdresse(dto.getAdresse());
        existant.setNumeroSecu(dto.getNumeroSecu());
        
        Patient misAJour = patientRepository.save(existant);
        return convertirToDTO(misAJour);
    }
    
    /**
     * DELETE : supprimer un patient.
     */
    public void supprimerPatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient non trouve avec l'ID : " + id);
        }
        patientRepository.deleteById(id);
    }
    
    /**
     * RECHERCHE : trouver par nom.
     */
    @Transactional(readOnly = true)
    public List<PatientDTO> rechercherParNom(String nom) {
        return patientRepository.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::convertirToDTO)
                .collect(Collectors.toList());
    }
    
    // ============================================
    // METHODES PRIVEES : CONVERSION DTO <-> ENTITY
    // ============================================
    
    private PatientDTO convertirToDTO(Patient patient) {
        return new PatientDTO(
            patient.getId(),
            patient.getNom(),
            patient.getPrenom(),
            patient.getDateNaissance(),
            patient.getTelephone(),
            patient.getEmail(),
            patient.getAdresse(),
            patient.getNumeroSecu()
        );
    }
    
    private Patient convertirToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setId(dto.getId());
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setTelephone(dto.getTelephone());
        patient.setEmail(dto.getEmail());
        patient.setAdresse(dto.getAdresse());
        patient.setNumeroSecu(dto.getNumeroSecu());
        return patient;
    }
}