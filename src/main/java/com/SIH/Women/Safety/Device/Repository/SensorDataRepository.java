package com.SIH.Women.Safety.Device.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.SensorData;

@Repository // <--- Sirf ye line consistency ke liye
public interface SensorDataRepository extends MongoRepository<SensorData, String> {
}