package com.SIH.Women.Safety.Device.Repository;

import com.SIH.Women.Safety.Device.model.Incident;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncidentRepository extends MongoRepository<Incident, String> {
}