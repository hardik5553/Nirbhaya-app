package com.SIH.Women.Safety.Device.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SIH.Women.Safety.Device.model.AudioVault;
import com.SIH.Women.Safety.Device.service.AudioEvidenceService;

@RestController
@RequestMapping("/api/evidence")
public class AudioEvidenceController {

    private final AudioEvidenceService evidenceService;

    public AudioEvidenceController(AudioEvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    // POST API: Multipart file upload (Postman / Browser App / Mobile App)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudioEvidence(
            @RequestParam(value = "userId", defaultValue = "user123") String userId,
            @RequestParam(value = "userEmail", required = false) String userEmail,
            @RequestParam("file") MultipartFile file) {
        
        String response = evidenceService.saveAudioEvidence(userId, userEmail, file);
        return ResponseEntity.ok(response);
    }

    // POST API: Binary bytes upload fallback
    @PostMapping("/upload-bytes")
    public ResponseEntity<String> uploadAudioBytes(
            @RequestParam(value = "userId", defaultValue = "user123") String userId,
            @RequestParam(value = "userEmail", required = false) String userEmail,
            @RequestBody byte[] fileBytes) {
        
        String response = evidenceService.saveAudioBytes(userId, userEmail, fileBytes);
        return ResponseEntity.ok(response);
    }

    // GET API: Fetch audio vault recordings by userId
    @GetMapping("/vault/{userId}")
    public ResponseEntity<List<AudioVault>> getVaultByUserId(@PathVariable String userId) {
        List<AudioVault> history = evidenceService.getVaultHistory(userId, null);
        return ResponseEntity.ok(history);
    }

    // GET API: Fetch audio vault recordings by userId or email query param
    @GetMapping("/vault")
    public ResponseEntity<List<AudioVault>> getVaultByQuery(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "email", required = false) String email) {
        List<AudioVault> history = evidenceService.getVaultHistory(userId, email);
        return ResponseEntity.ok(history);
    }

    // DELETE API: Delete an audio recording from vault
    @DeleteMapping("/vault/{id}")
    public ResponseEntity<?> deleteVaultRecord(@PathVariable String id) {
        boolean deleted = evidenceService.deleteVaultRecord(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Audio record deleted successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Record not found or could not be deleted."));
        }
    }
}