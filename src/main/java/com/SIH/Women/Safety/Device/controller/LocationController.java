package com.SIH.Women.Safety.Device.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.Repository.LocationUpdateRepository;
import com.SIH.Women.Safety.Device.model.LocationUpdate;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationUpdateRepository locationUpdateRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/update")
    public void receiveLocation(LocationUpdate update) {
        update.setTimestamp(LocalDateTime.now());
        locationUpdateRepository.save(update);
        messagingTemplate.convertAndSend("/topic/location/" + update.getUserId(), update);
    }

    @PostMapping("/update")
    public LocationUpdate receiveLocationRest(@RequestBody LocationUpdate update) {
        update.setTimestamp(LocalDateTime.now());
        LocationUpdate saved = locationUpdateRepository.save(update);
        messagingTemplate.convertAndSend("/topic/location/" + update.getUserId(), saved);
        return saved;
    }

    @GetMapping("/history/{userId}")
    public List<LocationUpdate> getHistory(@PathVariable String userId) {
        return locationUpdateRepository.findTop50ByUserIdOrderByTimestampDesc(userId);
    }
}