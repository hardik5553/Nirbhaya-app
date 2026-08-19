package com.SIH.Women.Safety.Device.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class AudioEvidenceService {

    @Autowired
    private Cloudinary cloudinary;

    public String saveAudioEvidence(String userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Error: No file uploaded or file is empty!";
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(), 
                ObjectUtils.asMap(
                    "folder", "smart_sos_evidence/" + userId,
                    "resource_type", "auto"
                )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            return "Evidence successfully uploaded to Cloudinary: " + secureUrl;

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload evidence to cloud: " + e.getMessage();
        }
    }

    public String saveAudioBytes(String userId, byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            return "Error: No data uploaded!";
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                fileBytes, 
                ObjectUtils.asMap(
                    "folder", "smart_sos_evidence/" + userId,
                    "resource_type", "auto"
                )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            return "Evidence bytes successfully uploaded to Cloudinary: " + secureUrl;

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload evidence bytes: " + e.getMessage();
        }
    }
}