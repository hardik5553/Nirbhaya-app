package com.SIH.Women.Safety.Device.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.Repository.UserLocationRepository;
import com.SIH.Women.Safety.Device.model.UserLocation;

@Service
public class UserLocationService {

    private final UserLocationRepository locationRepository;

    public UserLocationService(UserLocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // User ki live location update ya save karne ke liye
    public UserLocation updateLocation(String userId, double latitude, double longitude) {
        Optional<UserLocation> existingLocation = locationRepository.findByUserId(userId);
        UserLocation location;
        
        if (existingLocation.isPresent()) {
            location = existingLocation.get();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setTimestamp(LocalDateTime.now());
        } else {
            location = new UserLocation(userId, latitude, longitude);
        }
        
        return locationRepository.save(location);
    }

    // User ki latest location fetch karne ke liye
    public UserLocation getLocationByUserId(String userId) {
        return locationRepository.findByUserId(userId).orElse(null);
    }
}