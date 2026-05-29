package com.medical.gateway.controller;

import com.medical.gateway.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST pour l'authentification.
 * 
 * Endpoint unique (pour l'instant) :
 * - POST /api/auth/login → reçoit login/password, retourne JWT
 * 
 * Ce endpoint est PUBLIC (pas besoin de token pour y accéder).
 * C'est la porte d'entrée du système.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Authentification d'un utilisateur.
     * 
     * Requête attendue :
     * {
     *   "username": "dr.martin",
     *   "password": "password123"
     * }
     * 
     * Réponse :
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "type": "Bearer",
     *   "username": "dr.martin",
     *   "role": "MEDECIN"
     * }
     * 
     * @param request LoginRequest contenant username + password
     * @return ResponseEntity avec le token ou erreur 401
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.authenticate(
                    request.getUsername(), 
                    request.getPassword()
            );
            
            return ResponseEntity.ok(new LoginResponse(
                    token,
                    "Bearer",
                    request.getUsername(),
                    authService.getRoleFromToken(token)  // Méthode à ajouter
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // ============================================
    // DTOs internes (classes de transfert)
    // ============================================
    
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
    
    @Data
    @RequiredArgsConstructor
    public static class LoginResponse {
        private final String token;
        private final String type;
        private final String username;
        private final String role;
    }
    
    @Data
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final String message;
    }
}