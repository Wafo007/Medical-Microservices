package com.medical.patientservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entite JPA representant un patient dans la clinique.
 * 
 * @Entity : indique que cette classe est mappee sur une table SQL
 * @Table : nom explicite de la table en base
 * 
 * Lombok @Data genere automatiquement :
 * - getters, setters, toString, equals, hashCode
 */
@Entity
@Table(name = "patients")
@Data                    // Lombok : getters + setters + toString + equals + hashCode
@NoArgsConstructor       // Constructeur sans argument (requis par JPA)
@AllArgsConstructor      // Constructeur avec tous les arguments
public class Patient {
    
    /**
     * Cle primaire auto-generee.
     * IDENTITY = la base genere l'ID (auto-increment PostgreSQL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * @Column : personnalisation de la colonne SQL
     * nullable = false : champ obligatoire en base
     */
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    /**
     * Date de naissance du patient.
     * LocalDate = meilleur choix que Date legacy (Java 8+)
     */
    private LocalDate dateNaissance;
    
    private String telephone;
    
    private String email;
    
    /**
     * Adresse complete (simplifiee pour le devoir).
     * En production, on ferait une entite separee.
     */
    private String adresse;
    
    /**
     * Numero de securite sociale (identifiant unique medical).
     * unique = true : contrainte d'unicite en base
     */
    @Column(unique = true)
    private String numeroSecu;
    
    /**
     * Relation One-to-Many : un patient peut avoir plusieurs antecedents.
     * CascadeType.ALL : si on supprime le patient, ses antecedents aussi
     * orphanRemoval : supprime les antecedents orphelins
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Antecedent> antecedents = new ArrayList<>();
}