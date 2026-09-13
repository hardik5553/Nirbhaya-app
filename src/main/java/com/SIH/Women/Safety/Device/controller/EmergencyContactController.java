package com.SIH.Women.Safety.Device.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.model.EmergencyContact;
import com.SIH.Women.Safety.Device.service.EmergencyContactService;

@RestController
@RequestMapping("/api/emergency-contacts")
public class EmergencyContactController {

    private final EmergencyContactService contactService;

    public EmergencyContactController(EmergencyContactService contactService) {
        this.contactService = contactService;
    }

    // POST API: Emergency Contact add karne ke liye (/api/emergency-contacts/add/{userId})
    @PostMapping("/add/{userId}")
    public ResponseEntity<EmergencyContact> addContact(
            @PathVariable String userId,
            @RequestParam String name,
            @RequestParam String phoneNumber) {
        EmergencyContact contact = contactService.addContact(userId, name, phoneNumber);
        return ResponseEntity.ok(contact);
    }

    // GET API: User ke saare contacts fetch karne ke liye (/api/emergency-contacts/{userId})
    @GetMapping("/{userId}")
    public ResponseEntity<List<EmergencyContact>> getContacts(@PathVariable String userId) {
        List<EmergencyContact> contacts = contactService.getContactsByUserId(userId);
        return ResponseEntity.ok(contacts);
    }
}