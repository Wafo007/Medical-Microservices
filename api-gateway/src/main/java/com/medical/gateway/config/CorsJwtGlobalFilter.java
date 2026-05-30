package com.medical.gateway.config;

import com.medical.gateway.service.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * FILTRE GLOBAL : CORS + JWT combinés.
 * 
 * Priorité : Ordered.HIGHEST_PRECEDENCE = s'exécute EN PREMIER.
 * Cela garantit que les headers CORS sont ajoutés AVANT toute vérification JWT.
 * 
 * Architecture :
 * 1. Requête arrive
 * 2. Ce filtre s'exécute (priorité maximale)
 * 3. Ajoute les headers CORS à la réponse
 * 4. Si OPTIONS → termine immédiatement (200 OK)
 * 5. Si /api/auth/login → laisse passer sans JWT
 * 6. Sinon → vérifie le JWT
 */

@Slf4j
@Component
public class CorsJwtGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Origines autorisées (React)
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "*",
            "http://localhost:3000"
    );

    // Méthodes autorisées
    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    );

    // Headers autorisés
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Authorization", "Content-Type", "Accept", "Origin",
            "X-Requested-With", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
    );

    public CorsJwtGlobalFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public int getOrder() {
        // Priorité MAXIMALE : s'exécute avant tous les autres filtres
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        String origin = request.getHeaders().getOrigin();

        // ============================================
        // ÉTAPE 1 : AJOUTER LES HEADERS CORS (toujours)
        // ============================================
        // On ajoute les headers CORS à CHAQUE réponse, quelle que soit l'issue
        addCorsHeaders(response, origin);

        // ============================================
        // ÉTAPE 2 : GÉRER LE PREFLIGHT OPTIONS
        // ============================================
        // Les requêtes OPTIONS sont des "ping" du navigateur pour vérifier CORS
        // Elles ne doivent JAMAIS être bloquées par le JWT
        if (request.getMethod() == HttpMethod.OPTIONS) {
            log.debug("Preflight OPTIONS autorisé pour : {}", origin);
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete(); // Répond 200 OK immédiatement
        }

        // ============================================
        // ÉTAPE 3 : ROUTES PUBLIQUES (sans JWT)
        // ============================================
        if (path.startsWith("/api/auth/")) {
            log.debug("Route publique autorisée : {}", path);
            return chain.filter(exchange); // Continue sans vérifier le JWT
        }

        // ============================================
        // ÉTAPE 4 : VÉRIFICATION JWT (routes protégées)
        // ============================================
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JWT manquant pour : {}", path);
            return unauthorized(response, "Token manquant ou malformé");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            log.warn("JWT invalide pour : {}", path);
            return unauthorized(response, "Token invalide ou expiré");
        }

        // Ajoute les infos utilisateur dans les headers pour les services downstream
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Username", username)
                .header("X-User-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /**
     * Ajoute les headers CORS à la réponse.
     * Cette méthode est appelée pour TOUTES les requêtes.
     */
    private void addCorsHeaders(ServerHttpResponse response, String origin) {
        // Vérifie si l'origine est autorisée
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            response.getHeaders().set("Access-Control-Allow-Origin", origin);
        } else {
            // En développement, on autorise localhost:3000 par défaut
            response.getHeaders().set("Access-Control-Allow-Origin", "http://localhost:3000");
        }

        response.getHeaders().set("Access-Control-Allow-Methods", String.join(", ", ALLOWED_METHODS));
        response.getHeaders().set("Access-Control-Allow-Headers", String.join(", ", ALLOWED_HEADERS));
        response.getHeaders().set("Access-Control-Allow-Credentials", "true");
        response.getHeaders().set("Access-Control-Max-Age", "3600");
        response.getHeaders().set("Access-Control-Expose-Headers", "Authorization");
    }

    /**
     * Répond 401 Unauthorized avec les headers CORS déjà présents.
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set("Content-Type", "application/json");

        String body = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }
}