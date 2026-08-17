package com.SIH.Women.Safety.Device.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "riskSessions")
public class RiskSession {
    @Id
    private String id;
    private String userId;
    private double currentScore;
    private String zone; 
    private List<RiskEvent> activeEvents = new ArrayList<>();
    private LocalDateTime lastUpdated;
    private String activeIncidentId; 

    
    @Data
    public static class RiskEvent {
        private String type;      
        private double weight;
        private LocalDateTime timestamp;
    }
}