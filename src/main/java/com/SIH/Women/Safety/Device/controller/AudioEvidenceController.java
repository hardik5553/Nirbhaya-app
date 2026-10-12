package com.SIH.Women.Safety.Device.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAudioEvidence(
            @RequestParam("userId") String userId,
            @RequestParam(value = "userEmail", required = false) String userEmail,
            @RequestParam("file") MultipartFile file) {
        String audioUrl = evidenceService.saveAudioEvidence(userId, userEmail, file);
        return ResponseEntity.ok(Map.of("audioUrl", audioUrl));
    }

    @PostMapping("/upload-bytes")
    public ResponseEntity<Map<String, String>> uploadAudioBytes(
            @RequestParam("userId") String userId,
            @RequestParam(value = "userEmail", required = false) String userEmail,
            @RequestParam(value = "fileName", defaultValue = "evidence.bin") String fileName,
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestBody byte[] fileBytes) {
        String audioUrl = evidenceService.saveAudioBytes(userId, userEmail, fileBytes, contentType, fileName);
        return ResponseEntity.ok(Map.of("audioUrl", audioUrl));
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