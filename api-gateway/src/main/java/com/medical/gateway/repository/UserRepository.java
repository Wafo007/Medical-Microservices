package com.medical.gateway.repository;

import com.medical.gateway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour accéder aux utilisateurs en base.
 * 
 * findByUsername : Spring Data JPA génère automatiquement la requête :
 * SELECT * FROM users WHERE username = ?
 * 
 * Optional : évite les NullPointerException.
 * Si l'utilisateur n'existe pas → Optional.empty() au lieu de null.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
}