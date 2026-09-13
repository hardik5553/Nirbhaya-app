package com.SIH.Women.Safety.Device.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // Naya import
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.model.RiskEvaluationRequest; // Model import zaroori hai
import com.SIH.Women.Safety.Device.service.SmartSosEngineService;

@RestController
@RequestMapping("/api/smart-sos")
public class SmartSosController {

    private final SmartSosEngineService engineService;

    public SmartSosController(SmartSosEngineService engineService) {
        this.engineService = engineService;
    }

    // POST API: Thunder Client se aane wale poore JSON payload ko accept karne ke liye
    @PostMapping("/evaluate/{userId}")
    public ResponseEntity<String> evaluateSos(
            @PathVariable String userId,
            @RequestBody RiskEvaluationRequest request) { // <-- Edit yahan kiya hai
        
        // Service ko ab poora request object bhej rahe hain
        String result = engineService.evaluateSosTrigger(userId, request);
        return ResponseEntity.ok(result);
    }
}