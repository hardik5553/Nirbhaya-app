package com.SIH.Women.Safety.Device.model;

import lombok.Data;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "risk_sessions")
public class RiskSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private double currentScore;
    private String zone; 
    @ElementCollection
    @CollectionTable(name = "risk_session_events", joinColumns = @JoinColumn(name = "session_id"))
    private List<RiskEvent> activeEvents = new ArrayList<>();
    private LocalDateTime lastUpdated;
    private String activeIncidentId; 

    
    @Data
    @jakarta.persistence.Embeddable
    public static class RiskEvent {
        private String type;      
        private double weight;
        private LocalDateTime timestamp;
    }
}