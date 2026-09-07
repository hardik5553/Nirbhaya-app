package com.SIH.Women.Safety.Device.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.Repository.IncidentRepository;
import com.SIH.Women.Safety.Device.Repository.RiskSessionRepository;
import com.SIH.Women.Safety.Device.model.Incident;
import com.SIH.Women.Safety.Device.model.RiskEvaluationRequest;
import com.SIH.Women.Safety.Device.model.RiskSession; // ==> NAYA IMPORT YAHAN HAI

@Service
public class RiskSessionService {

    @Autowired
    private RiskSessionRepository riskSessionRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    private static final Map<String, Double> WEIGHTS = new HashMap<>();
    static {
        WEIGHTS.put("fall", 40.0);
        WEIGHTS.put("heartRateSpike", 30.0);
        WEIGHTS.put("noMovementAfterFall", 30.0);
        WEIGHTS.put("shake", 20.0);
        WEIGHTS.put("hidden_sos", 100.0);
        WEIGHTS.put("voice_sos", 100.0);
        WEIGHTS.put("manual_sos", 100.0);
    }

    private static final double DECAY_PER_MINUTE = 10.0;

    // ==========================================
    // 1. NAYA METHOD: Mega Request (All triggers) handle karne ke liye
    // ==========================================
    public RiskSession processComprehensiveRisk(String userId, RiskEvaluationRequest request) {
        RiskSession session = getCurrentSession(userId);
        applyDecay(session);

        double score = session.getCurrentScore();
        
        // Hardware & Physical Triggers Check
        if (request.isFallDetected()) score += WEIGHTS.getOrDefault("fall", 40.0);
        if (request.isShakeDetected()) score += WEIGHTS.getOrDefault("shake", 20.0);
        if (request.isNoMovementAfterFall()) score += WEIGHTS.getOrDefault("noMovementAfterFall", 30.0);
        if (request.getHeartRate() != null && request.getHeartRate() > 120) score += WEIGHTS.getOrDefault("heartRateSpike", 30.0);
        
        // Environmental & Location Triggers
        if (request.isHighRiskZone()) score += 20.0;
        if (request.isErraticMovementDetected()) score += 15.0;

        // Direct SOS Triggers (Immediate 100 Score)
        if (request.isManualSosTriggered() || request.isVoiceSosTriggered() || request.isHiddenSosTriggered() || request.isSilentSosTriggered() || request.isButtonClicked()) {
            score = 100.0;
        }

        session.setCurrentScore(Math.min(100, score)); // Score 100 se upar na jaye
        session.setLastUpdated(LocalDateTime.now());

        String previousZone = session.getZone();
        String newZone = calculateZone(session.getCurrentScore());
        session.setZone(newZone);

        // Agar Zone change hota hai (jaise Green se Red), toh actions lo
        if (!newZone.equals(previousZone)) {
            handleZoneTransition(session, newZone, request.getLatitude(), request.getLongitude());
        }

        return riskSessionRepository.save(session);
    }

    // ==========================================
    // 2. PURANA METHOD: Single events ke liye
    // ==========================================
    public RiskSession processEvent(String userId, String eventType, Double lat, Double lng) {
        RiskSession session = riskSessionRepository.findByUserId(userId)
                .orElseGet(() -> createNewSession(userId));

        applyDecay(session);

        double weight = WEIGHTS.getOrDefault(eventType, 10.0); // default small weight for unknown events
        session.setCurrentScore(Math.min(100, session.getCurrentScore() + weight));

        RiskSession.RiskEvent event = new RiskSession.RiskEvent();
        event.setType(eventType);
        event.setWeight(weight);
        event.setTimestamp(LocalDateTime.now());
        session.getActiveEvents().add(event);

        session.setLastUpdated(LocalDateTime.now());

        String previousZone = session.getZone();
        String newZone = calculateZone(session.getCurrentScore());
        session.setZone(newZone);

        if (!newZone.equals(previousZone)) {
            handleZoneTransition(session, newZone, lat, lng);
        }

        return riskSessionRepository.save(session);
    }

    private RiskSession createNewSession(String userId) {
        RiskSession session = new RiskSession();
        session.setUserId(userId);
        session.setCurrentScore(0);
        session.setZone("GREEN");
        session.setLastUpdated(LocalDateTime.now());
        return session;
    }

    private void applyDecay(RiskSession session) {
        if (session.getLastUpdated() == null) return;
        long minutesElapsed = Duration.between(session.getLastUpdated(), LocalDateTime.now()).toMinutes();
        if (minutesElapsed > 0) {
            double decayed = session.getCurrentScore() - (minutesElapsed * DECAY_PER_MINUTE);
            session.setCurrentScore(Math.max(0, decayed));
        }
    }

    private String calculateZone(double score) {
        if (score <= 30) return "GREEN";
        if (score <= 60) return "YELLOW";
        return "RED";
    }

    private void handleZoneTransition(RiskSession session, String newZone, Double lat, Double lng) {
        switch (newZone) {
            case "YELLOW":
                System.out.println("YELLOW zone: prompting user + sharing location with contacts for " + session.getUserId());
                break;
            case "RED":
                Incident incident = new Incident();
                incident.setUserId(session.getUserId());
                incident.setType("auto_sos");
                incident.setLatitude(lat); 
                incident.setLongitude(lng); 
                incident.setStatus("ACTIVE");
                incident.setTimestamp(LocalDateTime.now());
                Incident saved = incidentRepository.save(incident);
                session.setActiveIncidentId(saved.getId());
                System.out.println("RED zone: auto SOS triggered, incident " + saved.getId());
                break;
            default:
                break;
        }
    }

    public RiskSession getCurrentSession(String userId) {
        return riskSessionRepository.findByUserId(userId)
                .orElseGet(() -> createNewSession(userId));
    }
}