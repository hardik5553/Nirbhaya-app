package com.SIH.Women.Safety.Device.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "incidents")
public class Incident {

    @Id
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