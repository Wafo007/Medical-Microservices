package com.medical.gateway.service;

import com.medical.gateway.entity.Role;
import com.medical.gateway.entity.User;
import com.medical.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service métier de l'authentification.
 * 
 * Responsabilités :
 * 1. Vérifier les identifiants (login + password)
 * 2. Générer un JWT si les identifiants sont corrects
 * 3. Créer des utilisateurs de test au démarrage
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Crée des utilisateurs de test si la table est vide.
     * 
     * Cette méthode est appelée au démarrage de l'application
     * (via CommandLineRunner dans la classe principale).
     * 
     * Pourquoi ? Pour que tu puisses tester immédiatement
     * sans avoir à insérer manuellement des données en base.
     */
    public void initUsers() {
        if (userRepository.count() == 0) {
            log.info("Création des utilisateurs de test...");
            
            // Dr Martin - Médecin
            User medecin = new User();
            medecin.setUsername("dr.martin");
            medecin.setPassword(passwordEncoder.encode("password123"));
            medecin.setRole(Role.MEDECIN);
            medecin.setFullName("Dr Sophie Martin");
            userRepository.save(medecin);
            
            // Jean Dupont - Patient
            User patient = new User();
            patient.setUsername("jean.dupont");
            patient.setPassword(passwordEncoder.encode("password456"));
            patient.setRole(Role.PATIENT);
            patient.setFullName("Jean Dupont");
            userRepository.save(patient);
            
            // Admin test
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.MEDECIN);
            admin.setFullName("Administrateur");
            userRepository.save(admin);
            
            log.info("3 utilisateurs de test créés avec succès");
        }
    }
    
    /**
     * Authentifie un utilisateur et retourne un JWT.
     * 
     * Processus :
     * 1. Chercher l'utilisateur par son username
     * 2. Vérifier que le mot de passe correspond (bcrypt)
     * 3. Générer un JWT contenant username + rôle
     * 
     * @param username Nom d'utilisateur
     * @param password Mot de passe en clair (depuis le formulaire)
     * @return Le JWT signé
     * @throws RuntimeException si identifiants invalides
     */
    public String authenticate(String username, String password) {
        // Étape 1 : Chercher l'utilisateur
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            log.warn("Tentative de connexion échouée : utilisateur {} inconnu", username);
            throw new RuntimeException("Nom d'utilisateur ou mot de passe incorrect");
        }
        
        User user = userOpt.get();
        
        // Étape 2 : Vérifier le mot de passe
        // passwordEncoder.matches() compare le hash stocké avec le hash de la tentative
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Tentative de connexion échouée : mot de passe incorrect pour {}", username);
            throw new RuntimeException("Nom d'utilisateur ou mot de passe incorrect");
        }
        
        log.info("Authentification réussie : {} ({})", username, user.getRole());
        
        // Étape 3 : Générer le JWT
        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }
      /**
     * Extrait le rôle depuis un token (pour la réponse de login).
     */
    public String getRoleFromToken(String token) {
        return jwtUtil.extractRole(token);
    }
}