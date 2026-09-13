package com.SIH.Women.Safety.Device.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SIH.Women.Safety.Device.service.AudioEvidenceService;

@RestController
@RequestMapping("/api/evidence")
public class AudioEvidenceController {

    private final AudioEvidenceService evidenceService;

    public AudioEvidenceController(AudioEvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    // POST API: Multipart file upload karne ke liye (Postman/Mobile App use)
    // URL ko sirf "/upload" kar diya hai jisse Postman mein error na aaye
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudioEvidence(
            @RequestParam(value = "userId", defaultValue = "user123") String userId,
            @RequestParam("file") MultipartFile file) {
        
        String response = evidenceService.saveAudioEvidence(userId, file);
        return ResponseEntity.ok(response);
    }

    // POST API: Binary bytes accept karne ke liye (Thunder Client free version testing)
    @PostMapping("/upload-bytes")
    public ResponseEntity<String> uploadAudioBytes(
            @RequestParam(value = "userId", defaultValue = "user123") String userId,
            @RequestBody byte[] fileBytes) {
        
        String response = evidenceService.saveAudioBytes(userId, fileBytes);
        return ResponseEntity.ok(response);
    }
}