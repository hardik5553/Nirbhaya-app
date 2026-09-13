package com.SIH.Women.Safety.Device.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.model.EmergencyContact;
import com.SIH.Women.Safety.Device.Repository.EmergencyContactRepository;

@Service
public class EmergencyContactService {

    private final EmergencyContactRepository contactRepository;

    public EmergencyContactService(EmergencyContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public EmergencyContact addContact(String userId, String name, String phoneNumber) {
        EmergencyContact contact = new EmergencyContact(userId, name, phoneNumber);
        return contactRepository.save(contact);
    }

    public List<EmergencyContact> getContactsByUserId(String userId) {
        return contactRepository.findByUserId(userId);
    }
}