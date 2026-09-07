package com.SIH.Women.Safety.Device.controller;

import com.SIH.Women.Safety.Device.model.RiskSession;
import com.SIH.Women.Safety.Device.model.RiskEvaluationRequest; // Naya Model Import
import com.SIH.Women.Safety.Device.service.RiskSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk-session") // API consistency ke liye update kiya gaya
public class RiskSessionController {

    @Autowired
    private RiskSessionService riskSessionService;

    // ==========================================
    // 1. NAYA MEGA API: Saare Triggers Ek Sath Evaluate Karne Ke Liye
    // ==========================================
    @PostMapping("/evaluate/{userId}")
    public RiskSession evaluateFullRisk(
            @PathVariable String userId,
            @RequestBody RiskEvaluationRequest request) {
        
        // Service ka naya method jo poore AI model ka data ek sath process karega
        return riskSessionService.processComprehensiveRisk(userId, request);
    }

    // ==========================================
    // 2. PURANA API: Single Small Events (Fall, Shake) Ke Liye
    // ==========================================
    @PostMapping("/event")
    public RiskSession receiveEvent(@RequestBody RiskEventRequest request) {
        return riskSessionService.processEvent(
                request.userId, request.eventType, request.lat, request.lng
        );
    }

    @GetMapping("/current/{userId}")
    public RiskSession getCurrentRisk(@PathVariable String userId) {
        return riskSessionService.getCurrentSession(userId);
    }

    // Request body structure for incoming single events
    public static class RiskEventRequest {
        public String userId;
        public String eventType; 
        public Double lat;
        public Double lng;
    }
}