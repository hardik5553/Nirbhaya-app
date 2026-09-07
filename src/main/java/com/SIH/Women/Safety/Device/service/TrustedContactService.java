package com.SIH.Women.Safety.Device.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

// ==> SAHI IMPORTS YAHAN HAIN
import com.SIH.Women.Safety.Device.model.TrustedContact;
import com.SIH.Women.Safety.Device.Repository.TrustedContactRepository;

@Service
public class TrustedContactService {

    private final TrustedContactRepository repository;

    public TrustedContactService(TrustedContactRepository repository) {
        this.repository = repository;
    }

    // Naya contact add karne ka function
    public TrustedContact addContact(TrustedContact contact) {
        return repository.save(contact);
    }

    // Ek user ke saare contacts nikalne ka function
    public List<TrustedContact> getUserContacts(String userId) {
        return repository.findByUserId(userId);
    }

    // Database ke saare contacts nikalne ke liye
    public List<TrustedContact> getAllContacts() {
        return repository.findAll();
    }

    // ID ke basis par contact delete karne ka function
    public void deleteContact(String id) {
        repository.deleteById(id);
    }

    // Contact update karne ke liye (Optimized check)
    public TrustedContact updateContact(String id, TrustedContact updatedContact) {
        Optional<TrustedContact> existingContactOpt = repository.findById(id);
        
        if (existingContactOpt.isPresent()) {
            TrustedContact contact = existingContactOpt.get();
            contact.setName(updatedContact.getName());
            contact.setPhoneNumber(updatedContact.getPhoneNumber());
            contact.setRelationship(updatedContact.getRelationship());
            if (updatedContact.getUserId() != null) {
                contact.setUserId(updatedContact.getUserId());
            }
            return repository.save(contact);
        } else {
            throw new RuntimeException("Trusted contact not found with id: " + id);
        }
    }
}