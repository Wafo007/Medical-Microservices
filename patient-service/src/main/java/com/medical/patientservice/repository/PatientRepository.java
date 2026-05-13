package com.medical.patientservice.repository;

import  com.medical.patientservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA.
 * 
 * JpaRepository<Patient, Long> fournit GRATUITEMENT :
 * - save(), findById(), findAll(), deleteById(), count()...
 * - pagination, tri automatiques
 * 
 * On peut ajouter des methodes custom juste en declarant 
 * leur signature (Spring genere la requete SQL automatiquement!).
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Recherche par nom (contient, ignore la casse).
     * Spring Data JPA genere automatiquement la requete SQL :
     * SELECT * FROM patients WHERE LOWER(nom) LIKE LOWER('%?%')
     */
    List<Patient> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Recherche par numero de securite sociale (exact).
     */
    Patient findByNumeroSecu(String numeroSecu);
}