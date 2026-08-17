package com.SIH.Women.Safety.Device.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "incidents")
public class Incident {
    @Id
    private String id;
    private String userId;
    private String type;      
    private Double lat;
    private Double lng;
    private String status;    
    private LocalDateTime timestamp;
}