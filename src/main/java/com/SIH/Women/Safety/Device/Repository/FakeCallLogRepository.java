package com.SIH.Women.Safety.Device.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.FakeCallLog;

@Repository
public interface FakeCallLogRepository extends MongoRepository<FakeCallLog, String> {
}