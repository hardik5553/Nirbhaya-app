package com.SIH.Women.Safety.Device.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.SIH.Women.Safety.Device.model.DangerZone;

public interface DangerZoneRepository extends MongoRepository<DangerZone, String> {
}