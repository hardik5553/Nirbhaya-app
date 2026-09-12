package com.SIH.Women.Safety.Device.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.Repository.DangerZoneRepository;
import com.SIH.Women.Safety.Device.Repository.IncidentRepository;
import com.SIH.Women.Safety.Device.Repository.SosRepository;
import com.SIH.Women.Safety.Device.Repository.UserLocationRepository;
import com.SIH.Women.Safety.Device.model.DangerZone;
import com.SIH.Women.Safety.Device.model.DistressRequest;
import com.SIH.Women.Safety.Device.model.Incident;
import com.SIH.Women.Safety.Device.model.RoutePoint;
import com.SIH.Women.Safety.Device.model.RouteRequest;
import com.SIH.Women.Safety.Device.model.SosIncident;

@Service
public class SafetyFeatureService {
    private final DangerZoneRepository dangerZones;
    private final IncidentRepository incidents;
    private final UserLocationRepository locations;
    private final SosRepository sosRepository;

    public SafetyFeatureService(DangerZoneRepository dangerZones, IncidentRepository incidents,
                                UserLocationRepository locations, SosRepository sosRepository) {
        this.dangerZones = dangerZones;
        this.incidents = incidents;
        this.locations = locations;
        this.sosRepository = sosRepository;
    }

    public Map<String,Object> safeRoute(RouteRequest req) {
        List<RoutePoint> planned = Optional.ofNullable(req.getPlannedRoute()).orElse(List.of());
        List<RoutePoint> alternative = Optional.ofNullable(req.getAlternativeRoute()).orElse(List.of());

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("userId", req.getUserId());
        result.put("algorithm", "risk-aware route scoring");
        result.put("note", "Scores the supplied routes against configured geofences; it does not call an external maps routing engine.");

        int plannedRisk = routeRisk(planned);
        int alternativeRisk = routeRisk(alternative);

        result.put("plannedRouteRisk", plannedRisk);
        result.put("alternativeRouteRisk", alternativeRisk);
        result.put("recommendedRoute",
                alternative.isEmpty() || plannedRisk <= alternativeRisk ? "PLANNED_ROUTE" : "ALTERNATIVE_ROUTE");
        return result;
    }

    public Map<String,Object> deviation(String userId, double latitude, double longitude, List<RoutePoint> plannedRoute) {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("userId", userId);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        if (plannedRoute == null || plannedRoute.isEmpty()) {
            result.put("deviated", false);
            result.put("message", "No planned safe route was supplied.");
            return result;
        }
        double nearest = Double.MAX_VALUE;
        for (RoutePoint p : plannedRoute) {
            nearest = Math.min(nearest, distanceMeters(latitude, longitude, p.getLatitude(), p.getLongitude()));
        }
        result.put("nearestPlannedRouteDistanceMeters", Math.round(nearest * 100.0) / 100.0);
        result.put("deviated", nearest > 100.0);
        result.put("thresholdMeters", 100);
        return result;
    }

    private int routeRisk(List<RoutePoint> points) {
        int score = 0;
        for (RoutePoint p : points) {
            for (DangerZone z : dangerZones.findAll()) {
                if (distanceMeters(p.getLatitude(), p.getLongitude(), z.getCenterLat(), z.getCenterLng()) <= z.getRadiusMeters()) {
                    score += "HIGH".equalsIgnoreCase(z.getRiskLevel()) ? 3 : 1;
                }
            }
        }
        return score;
    }

    public Map<String,Object> nearbyHelp(double lat, double lng) {
        String base = lat + "," + lng;
        String encoded = java.net.URLEncoder.encode(base, java.nio.charset.StandardCharsets.UTF_8);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("latitude", lat);
        result.put("longitude", lng);
        result.put("police", "https://www.google.com/maps/search/?api=1&query=police+near+" + encoded);
        result.put("hospital", "https://www.google.com/maps/search/?api=1&query=hospital+near+" + encoded);
        result.put("shelter", "https://www.google.com/maps/search/?api=1&query=shelter+near+" + encoded);
        result.put("locationQuery", base);
        result.put("note", "Use the supplied coordinates in the map provider to find the nearest facility.");
        return result;
    }

    public Map<String,Object> distress(DistressRequest r) {
        int score = 0;
        List<String> signals = new ArrayList<>();
        if (r.isFallDetected()) { score += 50; signals.add("fall"); }
        if (r.isShakeDetected()) { score += 20; signals.add("shake"); }
        if (!r.isMovementDetected()) { score += 20; signals.add("no_movement"); }
        if (r.getAcceleration() >= 2.5) { score += 20; signals.add("high_acceleration"); }
        if (r.getHeartRate() >= 120) { score += 20; signals.add("elevated_heart_rate"); }
        if (r.getHeartRate() >= 150) { score += 20; signals.add("very_high_heart_rate"); }

        boolean trigger = score >= 50;
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("userId", r.getUserId());
        result.put("distressScore", Math.min(score, 100));
        result.put("distressDetected", trigger);
        result.put("signals", signals);
        result.put("recommendedAction", trigger ? "TRIGGER_EMERGENCY_FLOW" : "CONTINUE_MONITORING");
        return result;
    }

    public Map<String,Object> dangerZoneCheck(double lat, double lng) {
        Optional<DangerZone> zone = dangerZones.findAll().stream()
                .filter(z -> distanceMeters(lat, lng, z.getCenterLat(), z.getCenterLng()) <= z.getRadiusMeters())
                .findFirst();
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("latitude", lat);
        result.put("longitude", lng);
        result.put("insideDangerZone", zone.isPresent());
        zone.ifPresent(z -> {
            result.put("zoneId", z.getId());
            result.put("zoneName", z.getName());
            result.put("riskLevel", z.getRiskLevel());
            result.put("message", "Warning: you are entering a configured danger zone.");
        });
        return result;
    }

    public Map<String,Object> heatmap() {
        Map<String,Integer> cells = new LinkedHashMap<>();
        for (Incident i : incidents.findAll()) {
            String key = String.format(Locale.US, "%.2f,%.2f",
                    Math.floor(i.getLatitude()*100)/100.0, Math.floor(i.getLongitude()*100)/100.0);
            cells.merge(key, 1, Integer::sum);
        }
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("cellSizeDegrees", 0.01);
        result.put("cells", cells);
        result.put("source", "stored incidents");
        return result;
    }

    public List<Map<String,Object>> activeSosMap() {
        List<Map<String,Object>> out = new ArrayList<>();
        for (SosIncident sos : sosRepository.findAll()) {
            if (!("ACTIVE".equalsIgnoreCase(sos.getStatus()) || "SILENT_ACTIVE".equalsIgnoreCase(sos.getStatus())
                    || "PENDING_DISPATCH".equalsIgnoreCase(sos.getStatus()))) continue;
            Map<String,Object> item = new LinkedHashMap<>();
            item.put("incidentId", sos.getId());
            item.put("userId", sos.getUserId());
            item.put("status", sos.getStatus());
            item.put("silent", sos.isSilent());
            item.put("timestamp", sos.getTimestamp());
                com.SIH.Women.Safety.Device.model.UserLocation loc =
                    locations.findByUserId(sos.getUserId()).orElse(null);
            if (loc != null) {
                item.put("latitude", loc.getLatitude());
                item.put("longitude", loc.getLongitude());
                item.put("locationTimestamp", loc.getTimestamp());
            }
            out.add(item);
        }
        return out;
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000.0;
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dp = Math.toRadians(lat2-lat1), dl = Math.toRadians(lon2-lon1);
        double a = Math.sin(dp/2)*Math.sin(dp/2) + Math.cos(p1)*Math.cos(p2)*Math.sin(dl/2)*Math.sin(dl/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}