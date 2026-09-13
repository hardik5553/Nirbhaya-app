package com.SIH.Women.Safety.Device.controller;

import com.SIH.Women.Safety.Device.model.DistressRequest;
import com.SIH.Women.Safety.Device.model.RouteRequest;
import com.SIH.Women.Safety.Device.model.RoutePoint;
import com.SIH.Women.Safety.Device.service.SafetyFeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/safety")
public class SafetyFeatureController {
    
    private final SafetyFeatureService service;

    public SafetyFeatureController(SafetyFeatureService service) { 
        this.service = service; 
    }

    @PostMapping("/safe-route")
    public ResponseEntity<Map<String,Object>> safeRoute(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(service.safeRoute(request));
    }

    @GetMapping("/route-deviation/{userId}")
    public ResponseEntity<Map<String,Object>> routeDeviation(@PathVariable String userId,
                                                           @RequestParam double latitude,
                                                           @RequestParam double longitude,
                                                           @RequestBody(required = false) java.util.List<RoutePoint> plannedRoute) {
        return ResponseEntity.ok(service.deviation(userId, latitude, longitude, plannedRoute));
    }

    @GetMapping("/danger-zone-check")
    public ResponseEntity<Map<String,Object>> dangerZoneCheck(@RequestParam double latitude,
                                                             @RequestParam double longitude) {
        return ResponseEntity.ok(service.dangerZoneCheck(latitude, longitude));
    }

    @GetMapping("/nearby-help")
    public ResponseEntity<Map<String,Object>> nearbyHelp(@RequestParam double latitude,
                                                        @RequestParam double longitude) {
        return ResponseEntity.ok(service.nearbyHelp(latitude, longitude));
    }

    @PostMapping("/fall-detection")
    public ResponseEntity<Map<String,Object>> fallDetection(@RequestBody DistressRequest request) {
        Map<String,Object> result = new java.util.LinkedHashMap<>();
        result.put("userId", request.getUserId());
        result.put("fallDetected", request.isFallDetected());
        result.put("automaticEmergencyTrigger", request.isFallDetected());
        result.put("message", request.isFallDetected()
                ? "Fall detected. Start emergency confirmation/response flow."
                : "No fall detected.");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/heart-rate")
    public ResponseEntity<Map<String,Object>> heartRate(@RequestBody DistressRequest request) {
        int hr = request.getHeartRate();
        String level = hr >= 150 ? "HIGH" : (hr >= 120 ? "ELEVATED" : "NORMAL");
        Map<String,Object> result = new java.util.LinkedHashMap<>();
        result.put("userId", request.getUserId());
        result.put("heartRate", hr);
        result.put("stressIndicator", level);
        result.put("monitoringActive", true);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/distress")
    public ResponseEntity<Map<String,Object>> distress(@RequestBody DistressRequest request) {
        return ResponseEntity.ok(service.distress(request));
    }

    @GetMapping("/crime-heatmap")
    public ResponseEntity<Map<String,Object>> crimeHeatmap() {
        return ResponseEntity.ok(service.heatmap());
    }

    @GetMapping("/live-sos-map")
    public ResponseEntity<?> liveSosMap() {
        return ResponseEntity.ok(service.activeSosMap());
    }
}