package com.SIH.Women.Safety.Device.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.Repository.SensorDataRepository;
import com.SIH.Women.Safety.Device.model.SensorData;

@RestController
@RequestMapping("/api/sensor") // Updated for consistency
class SensorDataControllerImpl {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @PostMapping("/data")
    public SensorData receiveSensorData(@RequestBody SensorData data) {
        data.setTimestamp(LocalDateTime.now());
        return sensorDataRepository.save(data);
    }
}