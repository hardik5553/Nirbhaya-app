package com.SIH.Women.Safety.Device.model;

import lombok.Data;

@Data
public class RiskEvaluationRequest {
    
    // ==========================================
    // 1. LOCATION & MOVEMENT METRICS
    // ==========================================
    private double latitude;
    private double longitude;
    private boolean highRiskZone;            // Geofence / DangerZone se
    private boolean erraticMovementDetected; // GPS tracking anomalies
    private boolean isUserMoving;            // Smart SOS API se

    // ==========================================
    // 2. HARDWARE & ENVIRONMENTAL SENSORS
    // ==========================================
    private int batteryLevel;                // Smart SOS / Risk prediction se
    private int audioNoiseLevelDb;           // Shouting / High decibels check karne ke liye
    private Double heartRate;                // SensorData se (heartRateSpike detect karne ke liye)
    
    // ==========================================
    // 3. PHYSICAL ACCIDENT / INCIDENT TRIGGERS
    // ==========================================
    private boolean fallDetected;            // RiskSessionService ki weights se
    private boolean shakeDetected;           // Device shaking
    private boolean noMovementAfterFall;     // Critical accident logic

    // ==========================================
    // 4. SOS TRIGGERS (VARIOUS MODES)
    // ==========================================
    private boolean isButtonClicked;         // Smart SOS API (Physical wearable button)
    private boolean manualSosTriggered;      // App button click
    private boolean voiceSosTriggered;       // Voice command SOS
    private boolean hiddenSosTriggered;      // Hidden/Gestures SOS
    private boolean silentSosTriggered;      // Silent API se (bina alarm ke)

    // ==========================================
    // 5. CONNECTIVITY & SYSTEM STATUS
    // ==========================================
    private boolean isOfflineFallback;       // SmsController se (No internet/Network issue)
}