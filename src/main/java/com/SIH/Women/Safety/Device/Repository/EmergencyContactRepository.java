package com.SIH.Women.Safety.Device.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.EmergencyContact;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, String> {
    List<EmergencyContact> findByUserId(String userId);
}