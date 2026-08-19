package com.SIH.Women.Safety.Device.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.TrustedContact;

@Repository
public interface TrustedContactRepository extends MongoRepository<TrustedContact, String> {
    
    // Custom query user ke base par contacts nikalne ke liye
    List<TrustedContact> findByUserId(String userId);
    
    // MongoRepository ke paas pehle se hi findAll(), save(), deleteById() jaise methods hote hain, 
    // isiliye yahan alag se likhne ki zaroorat nahi hai.
}