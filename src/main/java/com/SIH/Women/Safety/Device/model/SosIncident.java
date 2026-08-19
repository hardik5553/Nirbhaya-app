package com.SIH.Women.Safety.Device.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sos_incidents")
public class SosIncident {

    @Id
    private String id;
    private String userId;
    private String status; // ACTIVE, RESOLVED, CANCELLED
    private boolean isSilent = false; // ==> NAYA FIELD Silent SOS ke liye
    private LocalDateTime timestamp;

    // Constructors
    public SosIncident() {
        this.timestamp = LocalDateTime.now();
        this.status = "ACTIVE";
        this.isSilent = false;
    }

    public SosIncident(String userId) {
        this.userId = userId;
        this.status = "ACTIVE";
        this.isSilent = false;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isSilent() { return isSilent; }
    public void setSilent(boolean silent) { isSilent = silent; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}