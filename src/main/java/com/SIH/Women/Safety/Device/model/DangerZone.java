package com.SIH.Women.Safety.Device.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "danger_zones")
public class DangerZone {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private Double centerLat;
    private Double centerLng;
    private Double radiusMeters; 
    private String riskLevel; 
}  
