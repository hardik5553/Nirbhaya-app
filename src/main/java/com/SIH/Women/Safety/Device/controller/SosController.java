package com.SIH.Women.Safety.Device.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.model.SosIncident;
import com.SIH.Women.Safety.Device.service.SosService;

@RestController
@RequestMapping("/api/sos")
public class SosController {

    private final SosService sosService;

    public SosController(SosService sosService) {
        this.sosService = sosService;
    }

    // POST API: One-Tap SOS Trigger karne ke liye (/api/sos/trigger)
    @PostMapping("/trigger")
    public ResponseEntity<SosIncident> triggerSos(@RequestBody Map<String, String> payload) {
        // JSON body se userId nikalne ke liye
        String userId = payload.get("userId");
        SosIncident incident = sosService.triggerSos(userId);
        return ResponseEntity.ok(incident);
    }

    // NAYA API: SOS Cancel karne ke liye (/api/sos/cancel/{id})
    @PostMapping("/cancel/{id}")
    public ResponseEntity<String> cancelSos(@PathVariable String id) {
        SosIncident cancelled = sosService.cancelSos(id);
        if (cancelled != null) {
            return ResponseEntity.ok("SOS Incident " + id + " has been cancelled.");
        } else {
            return ResponseEntity.badRequest().body("Incident not found or cannot be cancelled.");
        }
    }

    // NAYA API: Silent SOS Trigger karne ke liye (/api/sos/silent-trigger)
    @PostMapping("/silent-trigger")
    public ResponseEntity<SosIncident> triggerSilentSos(@RequestBody Map<String, String> payload) {
        // Ise bhi JSON body support ke liye update kar diya hai
        String userId = payload.get("userId");
        SosIncident incident = sosService.triggerSilentSos(userId);
        return ResponseEntity.ok(incident);
    }

    // ==> NAYA API: User ki SOS history/logs fetch karne ke liye (/api/sos/history/{userId})
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<SosIncident>> getSosHistory(@PathVariable String userId) {
        List<SosIncident> history = sosService.getSosHistory(userId);
        return ResponseEntity.ok(history);
    }
}