package com.medical.medecinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO pour un créneau de disponibilité.
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