package com.SIH.Women.Safety.Device.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.service.IncidentAnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class IncidentAnalyticsController {

    private final IncidentAnalyticsService analyticsService;

    public IncidentAnalyticsController(IncidentAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // GET API: User ke incident analytics aur timeline fetch karne ke liye (/api/analytics/{userId})
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getAnalytics(@PathVariable String userId) {
        // Backend terminal mein log print karne ke liye
        System.out.println("📊 Fetching Incident Analytics & Timeline for User: " + userId);
        
        Map<String, Object> analyticsData = analyticsService.getIncidentAnalytics(userId);
        
        return ResponseEntity.ok(analyticsData);
    }
}