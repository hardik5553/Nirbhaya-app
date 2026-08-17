
package com.SIH.Women.Safety.Device.controller;

import com.SIH.Women.Safety.Device.model.RiskSession;
import com.SIH.Women.Safety.Device.service.RiskSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/risk")
public class RiskSessionController {

    @Autowired
    private RiskSessionService riskSessionService;

    // Request body for incoming events
    public static class RiskEventRequest {
        public String userId;
        public String eventType; 
        public Double lat;
        public Double lng;
    }

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
}