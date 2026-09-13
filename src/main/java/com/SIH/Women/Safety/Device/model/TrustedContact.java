package com.SIH.Women.Safety.Device.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "trusted_contacts")
public class TrustedContact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String userId;       // Kis user ka contact hai ye
    private String name;         // Contact ka naam (jaise Mom, Friend)
    private String phoneNumber;  // Phone number jahan alert jayega
    private String relationship; // Relation (Family, Guardian, etc.)
}