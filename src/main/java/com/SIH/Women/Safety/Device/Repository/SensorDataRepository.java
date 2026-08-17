package com.SIH.Women.Safety.Device.Repository;

import com.SIH.Women.Safety.Device.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorDataRepository extends MongoRepository<SensorData, String> {
}
