package com.SIH.Women.Safety.Device.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class RiskPredictionService {

    // Purana method jo sirf coordinates check karta hai
    public Map<String, Object> calculateSafetyScore(double latitude, double longitude) {
        int safetyScore = 85; 
        String riskLevel = "LOW";
        String message = "Area appears safe. Normal monitoring active.";

        if (latitude > 26.9 || longitude < 80.8) {
            safetyScore = 45;
            riskLevel = "MODERATE";
            message = "Exercise caution. Moderate risk area detected.";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("latitude", latitude);
        response.put("longitude", longitude);
        response.put("safetyScore", safetyScore);
        response.put("riskLevel", riskLevel);
        response.put("message", message);
        
        return response;
    }

    // Updated Multi-Trigger aur AI Risk Prediction method (Controller ke sath synced)
    public Map<String, Object> evaluateRiskAndTriggerSos(
            double latitude, double longitude, 
            boolean erraticMovementDetected, boolean highRiskZone, 
            int audioNoiseLevelDb, int batteryLevel) {
        
        int dangerCount = 0;
        
        // Multi-trigger conditions check
        if (erraticMovementDetected) dangerCount++;
        if (highRiskZone) dangerCount++;
        if (audioNoiseLevelDb > 85) dangerCount++; // High shouting / noise
        if (batteryLevel < 15) dangerCount++; // Critically low battery

        String riskLevel;
        boolean triggerMainSos = false;

        if (dangerCount >= 2) {
            riskLevel = "CRITICAL";
            triggerMainSos = true; // Multiple conditions matched -> Auto-trigger main SOS!
        } else if (dangerCount == 1) {
            riskLevel = "HIGH";
        } else {
            riskLevel = "LOW";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("latitude", latitude);
        response.put("longitude", longitude);
        response.put("dangerConditionsMatched", dangerCount);
        response.put("riskLevel", riskLevel);
        response.put("autoSosTriggered", triggerMainSos);
        
        if (triggerMainSos) {
            response.put("message", "CRITICAL ALERT: Multiple danger parameters matched! Main SOS auto-triggered.");
        } else {
            response.put("message", "Risk levels evaluated. System monitoring active.");
        }

        return response;
    }
}