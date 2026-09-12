package com.SIH.Women.Safety.Device.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.SosIncident;

@Repository
public interface SosRepository extends JpaRepository<SosIncident, String> {
    
    // ==> NAYA METHOD: User ki saari SOS history latest timestamp ke hisab se nikalne ke liye
    List<SosIncident> findByUserIdOrderByTimestampDesc(String userId);
}