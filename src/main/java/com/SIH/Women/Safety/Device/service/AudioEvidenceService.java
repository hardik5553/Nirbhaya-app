package com.SIH.Women.Safety.Device.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SIH.Women.Safety.Device.Repository.AudioVaultRepository;
import com.SIH.Women.Safety.Device.Repository.UserRepository;
import com.SIH.Women.Safety.Device.model.AudioVault;

@Service
public class AudioEvidenceService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.storage-bucket:panic-evidence}")
    private String storageBucket;

    @Autowired
    private AudioVaultRepository audioVaultRepository;

    @Autowired
    private UserRepository userRepository;

    public AudioEvidenceService() {
    }

    public AudioEvidenceService(AudioVaultRepository audioVaultRepository, UserRepository userRepository) {
        this.audioVaultRepository = audioVaultRepository;
        this.userRepository = userRepository;
    }

    public String saveAudioEvidence(String userId, MultipartFile file) {
        return saveAudioEvidence(userId, null, file);
    }

    public String saveAudioEvidence(String userId, String userEmail, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Error: No file uploaded or file is empty!";
        }

        try {
            return upload(userId, userEmail, file.getBytes(), file.getContentType(), file.getOriginalFilename());
        } catch (IOException e) {
            return "Failed to upload evidence to Supabase Storage: " + e.getMessage();
        }
    }

    public String saveAudioBytes(String userId, byte[] fileBytes) {
        return saveAudioBytes(userId, null, fileBytes);
    }

    public String saveAudioBytes(String userId, String userEmail, byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            return "Error: No data uploaded!";
        }

        return upload(userId, userEmail, fileBytes, "application/octet-stream", "evidence.bin");
    }

    private String upload(String userId, String userEmail, byte[] bytes, String contentType, String originalName) {
        try {
            String safeName = (originalName == null || originalName.isBlank()) ? "evidence.bin" : originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String path = userId + "/" + UUID.randomUUID() + "-" + safeName;
            String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8).replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/storage/v1/object/" + storageBucket + "/" + encodedPath))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", contentType == null ? "application/octet-stream" : contentType)
                    .header("x-upsert", "true")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return "Supabase Storage upload failed: " + response.body();
            }

            // Create 7-day signed URL (604800 seconds) for private playback
            String audioUrl = createSignedUrl(encodedPath);
            if (audioUrl == null || audioUrl.isBlank() || audioUrl.startsWith("Evidence uploaded, but")) {
                // Fallback to direct object URL
                audioUrl = supabaseUrl + "/storage/v1/object/public/" + storageBucket + "/" + encodedPath;
            }

            // Determine effective user email for database indexing
            String effectiveEmail = userEmail;
            if ((effectiveEmail == null || effectiveEmail.isBlank()) && userRepository != null) {
                try {
                    effectiveEmail = userRepository.findById(userId)
                            .map(u -> u.getEmail())
                            .orElse(null);
                } catch (Exception ex) {
                    // Ignore lookup error
                }
            }
            if (effectiveEmail != null) {
                effectiveEmail = effectiveEmail.trim().toLowerCase();
            }

            // Persist into Supabase database audio_vault table
            if (audioVaultRepository != null) {
                try {
                    AudioVault vaultRecord = new AudioVault(userId, effectiveEmail, audioUrl, safeName);
                    audioVaultRepository.save(vaultRecord);
                } catch (Exception dbErr) {
                    System.err.println("Warning: Could not save audio_vault record to database: " + dbErr.getMessage());
                }
            }

            return audioUrl;
        } catch (Exception e) {
            return "Failed to upload evidence to Supabase Storage: " + e.getMessage();
        }
    }

    private String createSignedUrl(String encodedPath) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/storage/v1/object/sign/" + storageBucket + "/" + encodedPath))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"expiresIn\":604800}"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }

            Matcher matcher = Pattern.compile("\"signedURL\"\\s*:\\s*\"([^\"]+)\"").matcher(response.body());
            if (matcher.find()) {
                return supabaseUrl + "/storage/v1" + matcher.group(1).replace("\\/", "/");
            }
        } catch (Exception e) {
            System.err.println("Signed URL generation warning: " + e.getMessage());
        }
        return null;
    }

    public List<AudioVault> getVaultHistory(String userId, String userEmail) {
        if (audioVaultRepository == null) return Collections.emptyList();
        
        String cleanEmail = userEmail != null ? userEmail.trim().toLowerCase() : null;
        if (userId != null && !userId.isBlank() && cleanEmail != null && !cleanEmail.isBlank()) {
            return audioVaultRepository.findByUserIdOrUserEmailIgnoreCaseOrderByCreatedAtDesc(userId, cleanEmail);
        } else if (userId != null && !userId.isBlank()) {
            return audioVaultRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (cleanEmail != null && !cleanEmail.isBlank()) {
            return audioVaultRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(cleanEmail);
        }
        return Collections.emptyList();
    }

    public boolean deleteVaultRecord(String id) {
        if (audioVaultRepository == null || id == null || id.isBlank()) return false;
        Optional<AudioVault> record = audioVaultRepository.findById(id);
        if (record.isPresent()) {
            audioVaultRepository.deleteById(id);
            return true;
        }
        return false;
    }
}