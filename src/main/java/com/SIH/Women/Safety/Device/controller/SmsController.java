package com.SIH.Women.Safety.Device.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.service.SmsService;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    // POST API: Single Emergency SMS (Network Fallback Flag Ke Sath)
    @PostMapping("/send")
    public ResponseEntity<String> sendSms(
            @RequestParam String phoneNumber,
            @RequestParam String message,
            @RequestParam(defaultValue = "false") boolean isOfflineFallback) { 
        
        String response = smsService.sendEmergencySms(phoneNumber, message, isOfflineFallback);
        return ResponseEntity.ok(response);
    }

    // NAYA POST API: Ek sath sabhi Trusted Contacts ko Location Link ke sath SMS bhejna
    @PostMapping("/dispatch-sos")
    public ResponseEntity<String> dispatchSos(
            @RequestParam List<String> phoneNumbers,
            @RequestParam String latitude,
            @RequestParam String longitude) {
        
        smsService.dispatchSosToTrustedContacts(phoneNumbers, latitude, longitude);
        return ResponseEntity.ok("✅ Bulk SOS safely dispatched to all trusted contacts with exact Google Maps location!");
    }
}