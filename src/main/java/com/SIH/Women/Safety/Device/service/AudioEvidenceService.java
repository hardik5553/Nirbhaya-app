package com.SIH.Women.Safety.Device.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import com.SIH.Women.Safety.Device.Repository.AudioVaultRepository;
import com.SIH.Women.Safety.Device.Repository.UserRepository;
import com.SIH.Women.Safety.Device.model.AudioVault;

@Service
public class AudioEvidenceService {

    private static final long SIGNED_URL_EXPIRY_SECONDS = 604800;
    private static final Pattern SIGNED_URL_PATTERN = Pattern.compile("\"signedURL\"\\s*:\\s*\"([^\"]+)\"");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No evidence file was uploaded.");
        }

        try {
            return upload(userId, userEmail, file.getBytes(), file.getContentType(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The evidence file could not be read.", e);
        }
    }

    public String saveAudioBytes(String userId, byte[] fileBytes) {
        return saveAudioBytes(userId, null, fileBytes, "application/octet-stream", "evidence.bin");
    }

    public String saveAudioBytes(String userId, String userEmail, byte[] fileBytes, String contentType, String fileName) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No evidence data was uploaded.");
        }
        return upload(userId, userEmail, fileBytes, contentType, fileName);
    }

    private String upload(String userId, String userEmail, byte[] bytes, String contentType, String originalName) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user ID is required.");
        }
        if (audioVaultRepository == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Audio evidence database is not configured.");
        }
        requireStorageConfiguration();

        String safeUserId = sanitizePathSegment(userId);
        String safeName = sanitizeFileName(originalName);
        String path = safeUserId + "/" + UUID.randomUUID() + "-" + safeName;
        String encodedPath = encodeObjectPath(path);
        String baseUrl = supabaseUrl.replaceAll("/+$", "");
        boolean objectUploaded = false;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/storage/v1/object/" + encodePathSegment(storageBucket) + "/" + encodedPath))
                    .timeout(Duration.ofSeconds(45))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", contentType == null || contentType.isBlank() ? "application/octet-stream" : contentType)
                    .header("x-upsert", "true")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                    .build();

            HttpResponse<String> response = send(request);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Supabase Storage rejected the upload (HTTP " + response.statusCode() + ").");
            }
            objectUploaded = true;

            String audioUrl = createSignedUrl(path);
            String effectiveEmail = resolveEmail(userId, userEmail);
            audioVaultRepository.save(new AudioVault(userId, effectiveEmail, path, safeName));
            return audioUrl;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cleanupUploadedObject(baseUrl, encodedPath, objectUploaded);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "The evidence upload was interrupted.", e);
        } catch (Exception e) {
            cleanupUploadedObject(baseUrl, encodedPath, objectUploaded);
            if (e instanceof ResponseStatusException responseStatusException) {
                throw responseStatusException;
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Evidence could not be uploaded and saved.", e);
        }
    }

    private String resolveEmail(String userId, String userEmail) {
        String effectiveEmail = userEmail;
        if ((effectiveEmail == null || effectiveEmail.isBlank()) && userRepository != null) {
            try {
                effectiveEmail = userRepository.findById(userId).map(user -> user.getEmail()).orElse(null);
            } catch (Exception e) {
                System.err.println("Could not resolve the audio evidence owner's email: " + e.getMessage());
            }
        }
        return effectiveEmail == null ? null : effectiveEmail.trim().toLowerCase();
    }

    private String createSignedUrl(String path) {
        String baseUrl = supabaseUrl.replaceAll("/+$", "");
        String requestPath = "/storage/v1/object/sign/" + encodePathSegment(storageBucket) + "/" + encodeObjectPath(path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + requestPath))
                .timeout(Duration.ofSeconds(45))
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"expiresIn\":" + SIGNED_URL_EXPIRY_SECONDS + "}"))
                .build();

        try {
            HttpResponse<String> response = send(request);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Supabase could not create a private playback URL (HTTP " + response.statusCode() + ").");
            }

            Matcher matcher = SIGNED_URL_PATTERN.matcher(response.body());
            if (!matcher.find()) {
                throw new IllegalStateException("Supabase returned no private playback URL.");
            }

            String signedPath = matcher.group(1).replace("\\/", "/");
            return signedPath.startsWith("http") ? signedPath : baseUrl + "/storage/v1" + signedPath;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Supabase playback URL generation was interrupted.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Supabase playback URL could not be generated.", e);
        }
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void cleanupUploadedObject(String baseUrl, String encodedPath, boolean objectUploaded) {
        if (!objectUploaded) return;
        try {
            HttpRequest deleteRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/storage/v1/object/" + encodePathSegment(storageBucket) + "/" + encodedPath))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .DELETE()
                    .build();
            HttpResponse<String> response = send(deleteRequest);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.err.println("Could not remove an uncommitted audio object from Supabase Storage (HTTP "
                        + response.statusCode() + ").");
            }
        } catch (Exception cleanupError) {
            System.err.println("Could not remove an uncommitted audio object from Supabase Storage: "
                    + cleanupError.getMessage());
            if (cleanupError instanceof InterruptedException) Thread.currentThread().interrupt();
        }
    }

    private void requireStorageConfiguration() {
        if (supabaseUrl == null || supabaseUrl.isBlank() || serviceRoleKey == null || serviceRoleKey.isBlank()
                || storageBucket == null || storageBucket.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Audio evidence storage is not configured.");
        }
    }

    private String sanitizeFileName(String name) {
        String fileName = name == null || name.isBlank() ? "evidence.bin" : name.replace('\\', '/');
        int lastSlash = fileName.lastIndexOf('/');
        if (lastSlash >= 0) fileName = fileName.substring(lastSlash + 1);
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return fileName.isBlank() ? "evidence.bin" : fileName;
    }

    private String sanitizePathSegment(String value) {
        String segment = value.replaceAll("[^a-zA-Z0-9._-]", "_");
        return segment.isBlank() || segment.equals(".") || segment.equals("..") ? "user" : segment;
    }

    private String encodeObjectPath(String path) {
        return Arrays.stream(path.split("/"))
                .map(this::encodePathSegment)
                .collect(Collectors.joining("/"));
    }

    private String encodePathSegment(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public List<AudioVault> getVaultHistory(String userId, String userEmail) {
        if (audioVaultRepository == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Audio evidence database is not configured.");
        }

        String cleanEmail = userEmail != null ? userEmail.trim().toLowerCase() : null;
        List<AudioVault> records;
        if (userId != null && !userId.isBlank() && cleanEmail != null && !cleanEmail.isBlank()) {
            records = audioVaultRepository.findByUserIdOrUserEmailIgnoreCaseOrderByCreatedAtDesc(userId, cleanEmail);
        } else if (userId != null && !userId.isBlank()) {
            records = audioVaultRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (cleanEmail != null && !cleanEmail.isBlank()) {
            records = audioVaultRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(cleanEmail);
        } else {
            return Collections.emptyList();
        }

        requireStorageConfiguration();
        for (AudioVault record : records) {
            record.setAudioUrl(createSignedUrl(extractStoragePath(record.getAudioUrl())));
        }
        return records;
    }

    private String extractStoragePath(String storedValue) {
        if (storedValue == null || storedValue.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "An audio vault record has no storage path.");
        }

        String rawPath = storedValue;
        if (storedValue.startsWith("http://") || storedValue.startsWith("https://")) {
            try {
                rawPath = URI.create(storedValue).getRawPath();
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "An audio vault record has an invalid storage URL.", e);
            }
            String bucket = encodePathSegment(storageBucket);
            String signMarker = "/storage/v1/object/sign/" + bucket + "/";
            String publicMarker = "/storage/v1/object/public/" + bucket + "/";
            if (rawPath.startsWith(signMarker)) {
                rawPath = rawPath.substring(signMarker.length());
            } else if (rawPath.startsWith(publicMarker)) {
                rawPath = rawPath.substring(publicMarker.length());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "An audio vault record points outside the configured storage bucket.");
            }
        }

        return URLDecoder.decode(rawPath, StandardCharsets.UTF_8);
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
