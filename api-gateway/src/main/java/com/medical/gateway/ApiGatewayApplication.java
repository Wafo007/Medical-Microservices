package com.medical.gateway;

import com.medical.gateway.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    
    /**
     * CommandLineRunner : s'exécute au démarrage de l'application.
     * 
     * Crée les utilisateurs de test si la table est vide.
     * C'est l'équivalent d'un script d'initialisation de base.
     */
    @Bean
    public CommandLineRunner init(AuthService authService) {
        return args -> {
            authService.initUsers();
        };
    }
}