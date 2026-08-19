package com.SIH.Women.Safety.Device.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "locationUpdates")
public class LocationUpdate {
    @Id
    private String id;
    private String userId;
    private Double lat;
    private Double lng;
    private LocalDateTime timestamp;
}