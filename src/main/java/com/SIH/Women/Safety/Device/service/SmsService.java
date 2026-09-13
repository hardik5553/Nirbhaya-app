package com.SIH.Women.Safety.Device.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    // Offline / Emergency SMS trigger karne ka logic fallback flag ke sath
    public String sendEmergencySms(String phoneNumber, String message, boolean isOfflineFallback) {
        
        // Console mein saaf dikhega ki SMS normal internet se gaya ya Offline Fallback se
        if (isOfflineFallback) {
            System.out.println("⚠️ NETWORK ISSUE DETECTED! Triggering Offline SMS Fallback Gateway...");
        } else {
            System.out.println("✅ Triggering Standard SMS Gateway...");
        }

        // Yahan future mein Twilio, MSG91, ya Android Native SMS API integrate hogi.
        System.out.println("📱 Sending SMS to: " + phoneNumber);
        System.out.println("✉️ Message: " + message);
        System.out.println("-------------------------------------------------");
        
        return "SMS successfully sent to " + phoneNumber;
    }

    // Ek sath saare Trusted Contacts ko emergency message bhejne ka function
    public void dispatchSosToTrustedContacts(List<String> phoneNumbers, String latitude, String longitude) {
        // Location ke sath ek proper SOS message banaya
        String googleMapsLink = "https://maps.google.com/?q=" + latitude + "," + longitude;
        String emergencyMessage = "🚨 URGENT SOS! I am in danger. Please help me! Track my real-time location here: " + googleMapsLink;
        
        // Har number par loop chala kar SMS bhejenge (Offline mode true rakh kar)
        for (String phone : phoneNumbers) {
            sendEmergencySms(phone, emergencyMessage, true);
        }
    }
}