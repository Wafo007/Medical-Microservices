package com.medical.medecinservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un médecin dans la clinique.
 * 
 * Relation avec Disponibilite :
 * - Un médecin a plusieurs créneaux de disponibilité
 * - Ex: Dr Dupont est disponible lundi 9h-12h, mercredi 14h-18h...
 * 
 * @OneToMany = relation "un-à-plusieurs" (un médecin, plusieurs dispos)
 */
@Entity
@Table(name = "medecins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medecin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    /**
     * Spécialité médicale (cardiologie, dermatologie, etc.)
     * Enumérée en base comme STRING pour lisibilité
     * (plutôt que des nombres incompréhensibles)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialite specialite;
    
    private String telephone;
    
    private String email;
    
    /**
     * Numéro d'ordre des médecins (identifiant officiel unique)
     */
    @Column(unique = true, nullable = false)
    private String numeroOrdre;
    
    /**
     * Relation : un médecin a plusieurs disponibilités.
     * 
     * mappedBy = "medecin" → le champ 'medecin' dans Disponibilite
     *            possède la clé étrangère (côté propriétaire)
     * 
     * cascade = CascadeType.ALL → si on supprime le médecin,
     *           toutes ses disponibilités sont supprimées aussi
     * 
     * orphanRemoval = true → supprime les dispos orphelines
     *                  (qui n'ont plus de médecin associé)
     */
    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilite> disponibilites = new ArrayList<>();
}