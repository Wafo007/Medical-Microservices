package com.medical.rdvservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO Disponibilite pour le Feign Client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibiliteDTO {
    
    private Long id;
    private DayOfWeek jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
}