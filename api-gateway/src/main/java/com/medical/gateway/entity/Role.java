package com.medical.gateway.entity;

/**
 * Rôles possibles dans le système.
 * 
 * MEDECIN  : personnel médical, accès complet
 * PATIENT  : personne qui consulte, accès limité
 */
public enum Role {
    MEDECIN,
    PATIENT
}