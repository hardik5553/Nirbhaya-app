package com.SIH.Women.Safety.Device.service;

import com.SIH.Women.Safety.Device.model.DangerZone;
import com.SIH.Women.Safety.Device.Repository.DangerZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeofenceService {

    @Autowired
    private DangerZoneRepository dangerZoneRepository;

    // Returns the zone the user has entered, or empty if safe
    public Optional<DangerZone> checkLocation(double lat, double lng) {
        List<DangerZone> zones = dangerZoneRepository.findAll();
        for (DangerZone zone : zones) {
            double distance = haversineDistance(lat, lng, zone.getCenterLat(), zone.getCenterLng());
            if (distance <= zone.getRadiusMeters()) {
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    // Distance between two GPS points in meters
    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000; // Earth radius in meters
        double latDist = Math.toRadians(lat2 - lat1);
        double lngDist = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDist / 2) * Math.sin(latDist / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDist / 2) * Math.sin(lngDist / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}