package com.SIH.Women.Safety.Device.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.LocationUpdate;

@Repository // <--- Sirf ye ek line consistency ke liye add kar lo
public interface LocationUpdateRepository extends MongoRepository<LocationUpdate, String> {
    List<LocationUpdate> findTop50ByUserIdOrderByTimestampDesc(String userId);
}