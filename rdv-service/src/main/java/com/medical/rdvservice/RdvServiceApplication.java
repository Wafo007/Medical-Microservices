package com.medical.rdvservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Point d'entrée du microservice Rendez-vous.
 * 
 * @EnableFeignClients : active la découverte et le scan
 * des interfaces Feign (clients HTTP déclaratifs).
 * Sans cette annotation, les @FeignClient ne fonctionnent pas.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.medical.rdvservice.client")
public class RdvServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RdvServiceApplication.class, args);
    }
}