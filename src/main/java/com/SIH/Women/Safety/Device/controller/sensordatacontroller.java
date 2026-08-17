package com.SIH.Women.Safety.Device.controller;

import com.SIH.Women.Safety.Device.model.SensorData;
import com.SIH.Women.Safety.Device.Repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/sensor")
public class sensordatacontroller {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @PostMapping("/data")
    public SensorData receiveSensorData(@RequestBody SensorData data) {
        data.setTimestamp(LocalDateTime.now());
        return sensorDataRepository.save(data);
    }
}
