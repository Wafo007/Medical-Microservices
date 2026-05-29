package com.medical.gateway.security;

import com.medical.gateway.service.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Filtre Spring Cloud Gateway qui vérifie le JWT.
 * 
 * C'est le "gardien" de l'immeuble. Il intercepte CHAQUE requête
 * entrante et vérifie que le client a un badge (JWT) valide.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    private final JwtUtil jwtUtil;
    
    /**
     * Configuration du filtre (vide mais nécessaire pour le pattern).
     */
    public static class Config {
        // Configuration vide
    }
    
    /**
     * Constructeur avec injection de JwtUtil.
     * 
     * @param jwtUtil le service utilitaire JWT (injecté par Spring)
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            
            // ============================================
            // ROUTES PUBLIQUES (pas besoin de token)
            // ============================================
            if (path.equals("/api/auth/login")) {
                log.debug("Route publique autorisée sans token : {}", path);
                return chain.filter(exchange);
            }
            
            // ============================================
            // VÉRIFICATION DU TOKEN
            // ============================================
            
            // Étape 1 : Récupérer le header Authorization
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            // Étape 2 : Vérifier qu'il existe et commence par "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Requête rejetée : header Authorization manquant ou invalide pour {}", path);
                return unauthorized(exchange.getResponse(), "Token manquant ou malformé");
            }
            
            // Étape 3 : Extraire le token (supprime "Bearer ")
            String token = authHeader.substring(7);
            
            // Étape 4 : Vérifier la validité (signature + expiration)
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("Requête rejetée : token invalide ou expiré pour {}", path);
                return unauthorized(exchange.getResponse(), "Token invalide ou expiré");
            }
            
            // Étape 5 : Extraire les informations pour les transmettre
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            
            log.debug("Requête autorisée : {} (rôle {}) pour {}", username, role, path);
            
            // Étape 6 : Ajouter les infos utilisateur dans les headers
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Username", username)
                    .header("X-User-Role", role)
                    .build();
            
            // Continue vers le microservice avec la requête modifiée
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }
    
    /**
     * Retourne une réponse 401 Unauthorized.
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);
        
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }
}