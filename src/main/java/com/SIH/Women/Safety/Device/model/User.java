package com.SIH.Women.Safety.Device.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;                 // Supabase PostgreSQL UUID
    
    private String uniqueNumericId;    // Registration ke waqt generate hone wali 6-digit numeric ID
    private String username;           // Login ke liye use hoga
    private String email;              // Registration aur OTP ke liye
    private String password;           // Encrypted (BCrypt) password
    private String phoneNumber;        // Emergency communication ke liye
    private String otp;                // Registration / Forgot Password OTP
    private boolean verified;          // OTP verification status (True/False)
    
    @ElementCollection
    @CollectionTable(name = "user_trusted_contacts", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> trustedContactIds; // User ke trusted emergency contacts ki list
    private String defaultRiskLevel;        // Risk profile level (GREEN/YELLOW/RED)
    private String role;               // USER or ADMIN

    @Transient
    private String adminRegistrationKey;
}