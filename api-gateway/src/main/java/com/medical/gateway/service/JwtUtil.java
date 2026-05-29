package com.medical.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilitaire pour gérer les JWT (JSON Web Tokens).
 * 
 * Cette classe fait 2 choses essentielles :
 * 1. GÉNÉRER un token (quand l'utilisateur se connecte)
 * 2. VÉRIFIER un token (quand l'utilisateur fait une requête)
 * 
 * Un JWT est signé avec une clé secrète. Si quelqu'un modifie
 * le contenu du token, la signature ne correspondra plus → rejet.
 */
@Slf4j
@Component
public class JwtUtil {
    
    /**
     * Clé secrète injectée depuis application.yml.
     * Elle sert à SIGNER et VÉRIFIER les tokens.
     * 
     * ATTENTION : en production, cette clé doit être :
     * - Longue (au moins 256 bits pour HS256)
     * - Aléatoire et complexe
     * - Stockée dans une variable d'environnement
     */
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    /**
     * Convertit la chaîne secrète en objet SecretKey utilisable
     * par la bibliothèque jjwt.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    // ============================================
    // 1. GÉNÉRATION DU TOKEN
    // ============================================
    
    /**
     * Crée un nouveau JWT pour un utilisateur.
     * 
     * Contenu du token (payload) :
     * - subject (sub) : le username
     * - role : le rôle (MEDECIN ou PATIENT)
     * - issuedAt (iat) : date de création
     * - expiration (exp) : date d'expiration (24h par défaut)
     * 
     * Le tout est signé avec la clé secrète (HS256).
     * 
     * @param username Identifiant de l'utilisateur
     * @param role Rôle de l'utilisateur
     * @return Le token JWT sous forme de chaîne
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        String token = Jwts.builder()
                .subject(username)                    // "sub" : qui est-ce ?
                .claim("role", role)                  // "role" : MEDECIN ou PATIENT
                .issuedAt(now)                        // "iat" : quand créé ?
                .expiration(expiryDate)               // "exp" : quand expire ?
                .signWith(getSigningKey(), Jwts.SIG.HS256)  // Signature avec clé secrète
                .compact();                           // Compacte en chaîne
        
        log.debug("Token généré pour {} (expire le {})", username, expiryDate);
        return token;
    }
    
    // ============================================
    // 2. VÉRIFICATION DU TOKEN
    // ============================================
    
    /**
     * Extrait les informations (claims) d'un token.
     * 
     * Si le token est invalide (signature fausse, expiré,
     * malformé), cette méthode lance une exception.
     * 
     * @param token Le JWT reçu du client
     * @return Les claims (données contenues dans le token)
     * @throws Exception si le token est invalide
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Vérifie la signature avec notre clé
                .build()
                .parseSignedClaims(token)      // Parse et vérifie
                .getPayload();                 // Retourne le payload
    }
    
    /**
     * Vérifie si le token est valide (signature correcte + non expiré).
     * 
     * @param token Le JWT à vérifier
     * @return true si valide, false sinon
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());  // Vérifie expiration
        } catch (Exception e) {
            log.warn("Token invalide : {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrait le username du token.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    
    /**
     * Extrait le rôle du token.
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}