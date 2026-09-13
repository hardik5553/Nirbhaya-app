package com.SIH.Women.Safety.Device.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.model.RiskEvaluationRequest;
import com.SIH.Women.Safety.Device.service.RiskPredictionService;

@RestController
@RequestMapping("/api/risk")
public class RiskPredictionController {

    private final RiskPredictionService riskService;

    public RiskPredictionController(RiskPredictionService riskService) {
        this.riskService = riskService;
    }

    // GET API: Real-time safety score check karne ke liye (/api/risk/evaluate)
    @GetMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateRisk(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        
        Map<String, Object> result = riskService.calculateSafetyScore(latitude, longitude);
        return ResponseEntity.ok(result);
    }

    // POST API: Multi-trigger SOS aur AI Risk Prediction ke liye (/api/risk/evaluate-multi)
    @PostMapping("/evaluate-multi")
    public ResponseEntity<Map<String, Object>> evaluateMultiTriggerRisk(
            @RequestBody RiskEvaluationRequest request) {
        
        Map<String, Object> result = riskService.evaluateRiskAndTriggerSos(
                request.getLatitude(), 
                request.getLongitude(), 
                request.isErraticMovementDetected(), 
                request.isHighRiskZone(), 
                request.getAudioNoiseLevelDb(), 
                request.getBatteryLevel()
        );
        
        return ResponseEntity.ok(result);
    }
}