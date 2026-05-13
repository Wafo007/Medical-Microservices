package com.medical.patientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Point d'entree du microservice Patient.
 * 
 * @EnableDiscoveryClient : indique a Spring de s'enregistrer
 * aupres du serveur Eureka pour etre decouvert par les autres.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PatientServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }
}