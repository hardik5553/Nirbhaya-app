package com.SIH.Women.Safety.Device.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SIH.Women.Safety.Device.model.DangerZone;

public interface DangerZoneRepository extends JpaRepository<DangerZone, String> {
}