package com.SIH.Women.Safety.Device.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.UserLocation;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, String> {
    Optional<UserLocation> findByUserId(String userId);
}