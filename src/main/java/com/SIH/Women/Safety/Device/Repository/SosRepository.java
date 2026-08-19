package com.SIH.Women.Safety.Device.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.SosIncident;

@Repository
public interface SosRepository extends MongoRepository<SosIncident, String> {
    
    // ==> NAYA METHOD: User ki saari SOS history latest timestamp ke hisab se nikalne ke liye
    List<SosIncident> findByUserIdOrderByTimestampDesc(String userId);
}