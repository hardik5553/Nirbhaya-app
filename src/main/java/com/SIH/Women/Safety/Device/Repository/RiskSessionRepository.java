package com.SIH.Women.Safety.Device.Repository;

import com.SIH.Women.Safety.Device.model.RiskSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RiskSessionRepository extends JpaRepository<RiskSession, String> {
    Optional<RiskSession> findByUserId(String userId);
}