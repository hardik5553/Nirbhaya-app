package com.SIH.Women.Safety.Device.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.SensorData;

@Repository // <--- Sirf ye line consistency ke liye
public interface SensorDataRepository extends JpaRepository<SensorData, String> {
}