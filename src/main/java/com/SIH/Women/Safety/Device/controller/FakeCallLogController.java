package com.SIH.Women.Safety.Device.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.model.FakeCallLog;
import com.SIH.Women.Safety.Device.service.FakeCallLogService;

@RestController
@RequestMapping("/api/fake-call")
public class FakeCallLogController {

    private final FakeCallLogService fakeCallLogService;

    public FakeCallLogController(FakeCallLogService fakeCallLogService) {
        this.fakeCallLogService = fakeCallLogService;
    }

    // POST API: Fake call log trigger karne ke liye
    @PostMapping("/log/{userId}")
    public ResponseEntity<FakeCallLog> logFakeCall(
            @PathVariable String userId, 
            @RequestParam String callerName) {
        FakeCallLog log = fakeCallLogService.logFakeCall(userId, callerName);
        return ResponseEntity.ok(log);
    }
}