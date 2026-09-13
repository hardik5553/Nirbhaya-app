package com.SIH.Women.Safety.Device.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.model.Incident;
import com.SIH.Women.Safety.Device.Repository.IncidentRepository;

@Service
public class IncidentAnalyticsService {

    private final IncidentRepository incidentRepository;

    public IncidentAnalyticsService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // Analytics report aur Timeline generate karne ke liye
    public Map<String, Object> getIncidentAnalytics(String userId) {
        // Naya method use kiya: Latest incident sabse upar aayega (Timeline format)
        List<Incident> incidents = incidentRepository.findByUserIdOrderByTimestampDesc(userId);
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalIncidents", incidents.size());
        
        // Complex Logic 1: Status ke hisaab se incidents ko ginna (e.g., ACTIVE kitne, CANCELLED kitne)
        Map<String, Long> statusCounts = incidents.stream()
            .filter(i -> i.getStatus() != null)
            .collect(Collectors.groupingBy(Incident::getStatus, Collectors.counting()));
        report.put("statusBreakdown", statusCounts);
        
        // Complex Logic 2: Event Type ke hisaab se ginna (e.g., SOS_TRIGGERED kitne baar hua)
        Map<String, Long> eventTypeCounts = incidents.stream()
            .filter(i -> i.getEventType() != null)
            .collect(Collectors.groupingBy(Incident::getEventType, Collectors.counting()));
        report.put("eventTypeBreakdown", eventTypeCounts);
        
        // Poori sorted history (Timeline) bhej rahe hain
        report.put("incidentTimeline", incidents);
        
        return report;
    }
}