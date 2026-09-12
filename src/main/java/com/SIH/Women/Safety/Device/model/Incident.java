package com.SIH.Women.Safety.Device.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private String status; 
    private String type; 
    private Double latitude; 
    private Double longitude;
    private String eventType; 
    private String description; 
    private LocalDateTime timestamp;

    public Incident() {
        this.timestamp = LocalDateTime.now();
    }

    public Incident(String userId, String status, Double latitude, Double longitude) {
        this.userId = userId;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
    }

    public Incident(String userId, String status, Double latitude, Double longitude, String eventType, String description) {
        this.userId = userId;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventType = eventType;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}