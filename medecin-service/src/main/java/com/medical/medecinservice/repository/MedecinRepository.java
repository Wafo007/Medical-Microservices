package com.medical.medecinservice.repository;

import com.medical.medecinservice.entity.Medecin;
import com.medical.medecinservice.entity.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données des médecins.
 * 
 * Hérite de JpaRepository qui fournit les opérations CRUD de base.
 * 
 * Spring Data JPA génère AUTOMATIQUEMENT les requêtes SQL
 * à partir du nom des méthodes ! C'est la "magie" du framework.
 * 
 * Convention de nommage :
 * findBy + Champ + Opération
 * Ex: findBySpecialite → WHERE specialite = ?
 *     findByNomContainingIgnoreCase → WHERE LOWER(nom) LIKE LOWER(%?%)
 */
@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    
    /**
     * Rechercher les médecins par spécialité exacte.
     * 
     * Requête SQL générée :
     * SELECT * FROM medecins WHERE specialite = 'CARDIOLOGIE'
     */
    List<Medecin> findBySpecialite(Specialite specialite);
    
    /**
     * Rechercher par nom (partiel, insensible à la casse).
     * 
     * Requête SQL générée :
     * SELECT * FROM medecins WHERE LOWER(nom) LIKE LOWER('%dupont%')
     */
    List<Medecin> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Rechercher par spécialité ET nom.
     * 
     * Requête SQL générée :
     * SELECT * FROM medecins 
     * WHERE specialite = ? AND LOWER(nom) LIKE LOWER('%?%')
     */
    List<Medecin> findBySpecialiteAndNomContainingIgnoreCase(
            Specialite specialite, String nom);
}