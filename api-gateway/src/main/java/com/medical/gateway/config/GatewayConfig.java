package com.medical.gateway.config;

import com.medical.gateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des routes de la Gateway AVEC sécurité JWT.
 * 
 * Différence avec avant : chaque route applique maintenant
 * le JwtAuthenticationFilter qui vérifie le token.
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    
    private final JwtAuthenticationFilter jwtFilter;
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                
                // ============================================
                // ROUTE AUTH (publique, pas de filtre JWT)
                // ============================================
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("http://localhost:8080")  // Interne à la Gateway
                )
                
                // ============================================
                // ROUTE PATIENT (protégée par JWT)
                // ============================================
                .route("patient-service", r -> r
                        .path("/api/patients/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://PATIENT-SERVICE")
                )
                
                // ============================================
                // ROUTE MEDECIN (protégée par JWT)
                // ============================================
                .route("medecin-service", r -> r
                        .path("/api/medecins/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://MEDECIN-SERVICE")
                )
                
                // ============================================
                // ROUTE RDV (protégée par JWT)
                // ============================================
                .route("rdv-service", r -> r
                        .path("/api/rendezvous/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://RDV-SERVICE")
                )
                
                .build();
    }
}