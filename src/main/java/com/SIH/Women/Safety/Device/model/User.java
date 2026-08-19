package com.SIH.Women.Safety.Device.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    
    @Id
    private String id;                 // MongoDB ki standard unique ID
    
    private String uniqueNumericId;    // Registration ke waqt generate hone wali 6-digit numeric ID
    private String username;           // Login ke liye use hoga
    private String email;              // Registration aur OTP ke liye
    private String password;           // Encrypted (BCrypt) password
    private String phoneNumber;        // Emergency communication ke liye
    private String otp;                // Registration / Forgot Password OTP
    private boolean verified;          // OTP verification status (True/False)
    
    private List<String> trustedContactIds; // User ke trusted emergency contacts ki list
    private String defaultRiskLevel;        // Risk profile level (GREEN/YELLOW/RED)
}