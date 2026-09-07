package com.SIH.Women.Safety.Device.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.Repository.DangerZoneRepository;
import com.SIH.Women.Safety.Device.model.DangerZone;
import com.SIH.Women.Safety.Device.service.GeofenceService;

@RestController
@RequestMapping("/api/danger-zone")
public class DangerController {

    @Autowired
    private DangerZoneRepository dangerZoneRepository;

    @Autowired
    private GeofenceService geofenceService;

    @PostMapping("/add")
    public DangerZone addZone(@RequestBody DangerZone zone) {
        return dangerZoneRepository.save(zone);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, String>> checkLocation(@RequestParam double lat, @RequestParam double lng) {
        Optional<DangerZone> zone = geofenceService.checkLocation(lat, lng);
        Map<String, String> response = new HashMap<>();

        if (zone.isPresent()) {
            response.put("status", "DANGER");
            response.put("message", "You are in " + zone.get().getName());
            response.put("riskLevel", zone.get().getRiskLevel());
        } else {
            response.put("status", "SAFE");
            response.put("message", "No danger zone detected");
        }
        
        return ResponseEntity.ok(response);
    }
}