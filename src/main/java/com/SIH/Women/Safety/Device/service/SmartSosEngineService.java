package com.SIH.Women.Safety.Device.service;

import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.model.RiskEvaluationRequest;

@Service
public class SmartSosEngineService {

    // Smart evaluation logic jo poore JSON payload ko process karega
    public String evaluateSosTrigger(String userId, RiskEvaluationRequest request) {
        
        // 1. DIRECT SOS (Highest Priority)
        if (request.isButtonClicked() || request.isManualSosTriggered() || 
            request.isVoiceSosTriggered() || request.isHiddenSosTriggered() || 
            request.isSilentSosTriggered()) {
            return "CRITICAL: Direct SOS Triggered! Immediate help required for user: " + userId;
        }
        
        // 2. PHYSICAL ACCIDENTS & INJURIES
        if (request.isFallDetected() && request.isNoMovementAfterFall()) {
            return "CRITICAL: Severe fall detected with no subsequent movement! Auto-SOS triggered for user: " + userId;
        }

        if (request.isShakeDetected() && request.getAudioNoiseLevelDb() > 80) {
            return "HIGH RISK: Device shaking violently with high surrounding noise. Possible struggle detected for: " + userId;
        }

        // 3. SENSORS & PANIC DETECTION
        if (request.getHeartRate() != null && request.getHeartRate() > 120 && request.getAudioNoiseLevelDb() > 85) {
            return "CRITICAL: High heart rate and screaming/high noise detected. User panic detected: " + userId;
        }

        // 4. LOCATION & GEOFENCING
        if (request.isHighRiskZone() && request.isErraticMovementDetected()) {
            return "HIGH RISK: User is in a danger zone with abnormal movement patterns. System on high alert for: " + userId;
        }

        // 5. HARDWARE & CONNECTIVITY WARNINGS
        if (request.getBatteryLevel() < 15 && request.isHighRiskZone()) {
            return "WARNING: Critically low battery in a high-risk area. Pre-emptive location sharing advised for: " + userId;
        }

        if (request.isOfflineFallback()) {
            return "WARNING: Device is offline. SMS network fallback routing activated for user: " + userId;
        }

        // 6. NORMAL STATE
        return "SAFE: All parameters normal. No immediate emergency detected for user: " + userId;
    }
}