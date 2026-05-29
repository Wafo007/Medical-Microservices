package com.medical.gateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un utilisateur du système (médecin ou patient).
 * 
 * Cette table stocke les identifiants de connexion.
 * Elle est SÉPARÉE des tables patients/médecins car :
 * - Un médecin existe dans "db_medecins" (données métier)
 * - Le Dr Martin existe aussi ici (identifiant de connexion)
 * 
 * En production, on ferait une relation entre User et Medecin/Patient.
 * Pour le devoir, on garde simple : username = nom du médecin/patient.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nom d'utilisateur unique (identifiant de connexion).
     * Ex: "dr.martin", "jean.dupont"
     */
    @Column(unique = true, nullable = false)
    private String username;
    
    /**
     * Mot de passe HASHÉ (jamais en clair !).
     * 
     * Explication du hash bcrypt :
     * - "password123" → "$2a$10$N9qo8uLOickgx2ZMRZoMy.Mqr..."
     * - C'est IRRÉVERSIBLE : on ne peut pas retrouver "password123"
     * - Pour vérifier : on hash la tentative et on compare les hashes
     * - Le hash contient le "salt" (sel) aléatoire : même mot de passe
     *   = hash différent pour chaque utilisateur (sécurité++)
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * Rôle de l'utilisateur.
     * 
     * Spring Security attend le préfixe "ROLE_" automatiquement.
     * On stocke sans préfixe en base, Spring l'ajoute en mémoire.
     * 
     * MEDECIN  → peut tout voir, tout modifier
     * PATIENT  → ne peut voir que ses propres données (futur)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    /**
     * Nom complet (affichage dans l'interface).
     */
    private String fullName;
}
