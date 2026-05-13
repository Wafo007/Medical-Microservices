package com.medical.medecinservice.repository;

import com.medical.medecinservice.entity.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Repository pour les disponibilités.
 * 
 * Permet de trouver les créneaux horaires d'un médecin
 * ou de savoir qui est disponible un jour précis.
 */
@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    
    /**
     * Toutes les disponibilités d'un médecin donné.
     */
    List<Disponibilite> findByMedecinId(Long medecinId);
    
    /**
     * Médecins disponibles un jour précis (ex: tous les lundis).
     * Utile pour la planification des rendez-vous.
     */
    List<Disponibilite> findByJour(DayOfWeek jour);
}