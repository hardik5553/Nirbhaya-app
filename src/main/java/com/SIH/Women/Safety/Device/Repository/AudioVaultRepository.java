package com.SIH.Women.Safety.Device.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.AudioVault;

@Repository
public interface AudioVaultRepository extends JpaRepository<AudioVault, String> {

    List<AudioVault> findByUserIdOrderByCreatedAtDesc(String userId);

    List<AudioVault> findByUserEmailIgnoreCaseOrderByCreatedAtDesc(String userEmail);

    List<AudioVault> findByUserIdOrUserEmailIgnoreCaseOrderByCreatedAtDesc(String userId, String userEmail);
}

