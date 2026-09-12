package com.SIH.Women.Safety.Device.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AudioEvidenceService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.storage-bucket:panic-evidence}")
    private String storageBucket;

    public String saveAudioEvidence(String userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Error: No file uploaded or file is empty!";
        }

        try { return upload(userId, file.getBytes(), file.getContentType(), file.getOriginalFilename()); }
        catch (IOException e) { return "Failed to upload evidence to Supabase Storage: " + e.getMessage(); }
    }

    public String saveAudioBytes(String userId, byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            return "Error: No data uploaded!";
        }

        return upload(userId, fileBytes, "application/octet-stream", "evidence.bin");
    }

    private String upload(String userId, byte[] bytes, String contentType, String originalName) {
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
            if (response.statusCode() < 200 || response.statusCode() >= 300) return "Supabase Storage upload failed: " + response.body();
            return createSignedUrl(encodedPath);
        } catch (Exception e) {
            return "Failed to upload evidence to Supabase Storage: " + e.getMessage();
        }
    }

    private String createSignedUrl(String encodedPath) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(supabaseUrl + "/storage/v1/object/sign/" + storageBucket + "/" + encodedPath))
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"expiresIn\":3600}"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) return "Evidence uploaded, but signed URL creation failed.";
        Matcher matcher = Pattern.compile("\\\"signedURL\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"").matcher(response.body());
        return matcher.find() ? supabaseUrl + "/storage/v1" + matcher.group(1).replace("\\/", "/") : "Evidence uploaded to private Supabase Storage.";
    }
}