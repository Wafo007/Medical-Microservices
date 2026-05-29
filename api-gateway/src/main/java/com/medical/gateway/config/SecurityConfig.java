package com.medical.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de Spring Security pour la Gateway.
 * 
 * @EnableWebFluxSecurity : active la sécurité réactive (WebFlux)
 * car Spring Cloud Gateway est basé sur WebFlux (pas Spring MVC).
 * 
 * Cette classe définit :
 * 1. L'encodeur de mot de passe (bcrypt)
 * 2. Les règles de sécurité (quels endpoints sont publics/protégés)
 * 
 * NOTE : Le filtrage JWT principal est fait par JwtAuthenticationFilter
 * (composant Gateway), pas par Spring Security directement.
 * Cette configuration est nécessaire pour désactiver CSRF et
 * configurer l'authentification stateless (sans session).
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    /**
     * Encodeur de mot de passe BCrypt.
     * 
     * BCrypt est l'algorithme standard en 2024 car :
     * - Il hache avec un "salt" aléatoire (même mot de passe = hash différent)
     * - Il est lent volontairement (résiste aux attaques par force brute)
     * - Il est adaptatif (on peut augmenter la complexité avec le temps)
     * 
     * Exemple :
     *   encode("password123") → "$2a$10$N9qo8uLOickgx2ZMRZoMy.Mqr..."
     *   matches("password123", hash) → true
     *   matches("wrong", hash) → false
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Chaîne de filtres de sécurité.
     * 
     * On désactive plusieurs protections par défaut car :
     * - CSRF : inutile pour une API REST (pas de formulaires HTML)
     * - Form login : on utilise JWT, pas de sessions
     * - HTTP Basic : on utilise JWT, pas de login/password par requête
     * 
     * La vraie sécurité est assurée par notre JwtAuthenticationFilter
     * qui s'exécute au niveau Gateway.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())           // Désactive CSRF (API REST)
                .formLogin(form -> form.disable())      // Désactive login formulaire
                .httpBasic(basic -> basic.disable())    // Désactive HTTP Basic
                .authorizeExchange(exchanges -> 
                    exchanges
                        .pathMatchers("/api/auth/**").permitAll()  // Login public
                        .anyExchange().permitAll()                  // Le reste : filtre JWT gère
                )
                .build();
    }
}