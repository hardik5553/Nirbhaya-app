package com.SIH.Women.Safety.Device.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "dangerZones")
public class DangerZone {
    @Id
    private String id;
    private String name;
    private Double centerLat;
    private Double centerLng;
    private Double radiusMeters; 
    private String riskLevel; 
}  
