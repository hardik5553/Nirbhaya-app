package com.SIH.Women.Safety.Device.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.model.UserLocation;
import com.SIH.Women.Safety.Device.service.UserLocationService;

@RestController
@RequestMapping("/api/location")
public class UserLocationController {

    private final UserLocationService locationService;

    public UserLocationController(UserLocationService locationService) {
        this.locationService = locationService;
    }

    // POST API: Live location update karne ke liye (/api/location/update/{userId})
    @PostMapping("/update/{userId}")
    public ResponseEntity<UserLocation> updateLocation(
            @PathVariable String userId,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        UserLocation location = locationService.updateLocation(userId, latitude, longitude);
        return ResponseEntity.ok(location);
    }

    // GET API: User ki current location fetch karne ke liye (/api/location/{userId})
    @GetMapping("/{userId}")
    public ResponseEntity<UserLocation> getLocation(@PathVariable String userId) {
        UserLocation location = locationService.getLocationByUserId(userId);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return ResponseEntity.notFound().build();
    }
}