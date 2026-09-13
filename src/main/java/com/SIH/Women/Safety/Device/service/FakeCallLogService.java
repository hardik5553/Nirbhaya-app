package com.SIH.Women.Safety.Device.service;

import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.Repository.FakeCallLogRepository;
import com.SIH.Women.Safety.Device.model.FakeCallLog;

@Service
public class FakeCallLogService {

    private final FakeCallLogRepository fakeCallLogRepository;

    public FakeCallLogService(FakeCallLogRepository fakeCallLogRepository) {
        this.fakeCallLogRepository = fakeCallLogRepository;
    }

    // Fake call log save karne ka function
    public FakeCallLog logFakeCall(String userId, String callerName) {
        FakeCallLog log = new FakeCallLog(userId, callerName);
        return fakeCallLogRepository.save(log);
    }
}