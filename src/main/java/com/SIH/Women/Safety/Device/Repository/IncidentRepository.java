package com.SIH.Women.Safety.Device.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.Incident;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, String> {
    
    List<Incident> findByUserId(String userId);
    
    List<Incident> findByUserIdOrderByTimestampDesc(String userId);
}