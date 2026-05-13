package com.medical.medecinservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Point d'entrée du microservice Médecin.
 * 
 * Architecture : ce service gère indépendamment les données
 * des médecins (spécialités, disponibilités). Il ne connaît
 * pas l'existence des autres services (découplage).
 */
@SpringBootApplication
@EnableDiscoveryClient  // S'enregistre auprès d'Eureka
public class MedecinServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MedecinServiceApplication.class, args);
    }
}