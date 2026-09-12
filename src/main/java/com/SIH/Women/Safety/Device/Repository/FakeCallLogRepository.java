package com.SIH.Women.Safety.Device.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.FakeCallLog;

@Repository
public interface FakeCallLogRepository extends JpaRepository<FakeCallLog, String> {
}