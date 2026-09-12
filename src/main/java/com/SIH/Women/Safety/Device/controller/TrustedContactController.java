package com.SIH.Women.Safety.Device.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.model.TrustedContact;
import com.SIH.Women.Safety.Device.service.TrustedContactService;

@RestController
@RequestMapping("/api/trusted-contacts")
public class TrustedContactController {

    private final TrustedContactService service;

    public TrustedContactController(TrustedContactService service) {
        this.service = service;
    }

    // POST API: Naya contact save karne ke liye
    @PostMapping("/add")
    public ResponseEntity<TrustedContact> addContact(@RequestBody TrustedContact contact) {
        TrustedContact savedContact = service.addContact(contact);
        return ResponseEntity.ok(savedContact);
    }

    // GET API: User ke contacts dekhne ke liye (By userId)
    @GetMapping("/{userId}")
    public ResponseEntity<List<TrustedContact>> getContacts(@PathVariable String userId) {
        return ResponseEntity.ok(service.getUserContacts(userId));
    }

    // GET API: Database ke saare contacts dekhne ke liye (/api/trusted-contacts/all)
    @GetMapping("/all")
    public ResponseEntity<List<TrustedContact>> getAllContacts() {
        return ResponseEntity.ok(service.getAllContacts());
    }

    // DELETE API: ID ke basis par contact delete karne ke liye
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable String id) {
        service.deleteContact(id);
        return ResponseEntity.ok("Contact successfully deleted with id: " + id);
    }

    // PUT API: Contact update karne ke liye (/api/trusted-contacts/update/{id})
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateContact(@PathVariable String id, @RequestBody TrustedContact contact) {
        try {
            TrustedContact updatedContact = service.updateContact(id, contact);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}