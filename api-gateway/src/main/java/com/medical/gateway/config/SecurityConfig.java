package com.medical.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration Spring Security + CORS pour API Gateway (WebFlux).
 * 
 * CORS est configuré ICI, au niveau de Spring Security, qui s'exécute
 * AVANT les filtres Gateway. Cela garantit que les requêtes OPTIONS
 * reçoivent les headers CORS avant toute autre vérification.
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration CORS - CRITIQUE pour le frontend React.
     * Cette configuration est appliquée à TOUTES les requêtes,
     * y compris les OPTIONS (preflight).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Origines autorisées (React)
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        
        // Méthodes autorisées
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Headers autorisés
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "Origin",
            "X-Requested-With", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Expose le header Authorization (pour que React puisse le lire)
        config.setExposedHeaders(List.of("Authorization"));
        
        // Autorise les cookies/credentials
        config.setAllowCredentials(true);
        
        // Cache du preflight (1 heure)
        config.setMaxAge(3600L);

        // Applique à toutes les URLs
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return source;
    }

    /**
     * Chaîne de filtres de sécurité.
     * 
     * ORDRE IMPORTANT :
     * 1. CORS (cors()) → ajoute les headers CORS
     * 2. CSRF désactivé (pas besoin pour API REST)
     * 3. Autorise /api/auth/login sans authentification
     * 4. Tout le reste nécessite authentification
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // ÉTAPE 1 : CORS en PREMIER
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // ÉTAPE 2 : Désactive CSRF (API REST stateless)
                .csrf(csrf -> csrf.disable())
                
                // ÉTAPE 3 : Configuration des autorisations
                .authorizeExchange(exchanges -> exchanges
                        // Routes publiques (pas de token nécessaire)
                        .pathMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        // Tout le reste nécessite authentification
                        .anyExchange().authenticated()
                )
                
                // ÉTAPE 4 : Désactive le login par formulaire (on utilise JWT)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                
                .build();
    }
}