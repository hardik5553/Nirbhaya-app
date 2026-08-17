package com.SIH.Women.Safety.Device.Repository;

import com.SIH.Women.Safety.Device.model.RiskSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RiskSessionRepository extends MongoRepository<RiskSession, String> {
    Optional<RiskSession> findByUserId(String userId);
}