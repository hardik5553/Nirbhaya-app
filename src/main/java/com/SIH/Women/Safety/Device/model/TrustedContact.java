package com.SIH.Women.Safety.Device.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "trusted_contacts")
public class TrustedContact {

    @Id
    private String id;
    
    private String userId;       // Kis user ka contact hai ye
    private String name;         // Contact ka naam (jaise Mom, Friend)
    private String phoneNumber;  // Phone number jahan alert jayega
    private String relationship; // Relation (Family, Guardian, etc.)
}