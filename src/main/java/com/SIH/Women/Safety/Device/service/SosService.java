package com.SIH.Women.Safety.Device.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.SIH.Women.Safety.Device.model.SosIncident;
import com.SIH.Women.Safety.Device.Repository.SosRepository;

@Service
public class SosService {

    private final SosRepository sosRepository;
    
    // Background timer thread pool aur active timers track karne ke liye map
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private final Map<String, ScheduledFuture<?>> activeSosTasks = new ConcurrentHashMap<>();

    public SosService(SosRepository sosRepository) {
        this.sosRepository = sosRepository;
    }

    // SOS Trigger karne ka function (15 seconds delay timer ke sath)
    public SosIncident triggerSos(String userId) {
        // Pehle incident create karke PENDING/ACTIVE state me save karo
        SosIncident incident = new SosIncident(userId);
        incident.setStatus("PENDING_DISPATCH"); // 15 sec ke window ke liye status
        SosIncident savedIncident = sosRepository.save(incident);
        String incidentId = savedIncident.getId();

        System.out.println("🚨 SOS Initiated for user: " + userId + ". 15 seconds cancellation window started! [ID: " + incidentId + "]");

        // 15 second ka background task schedule kiya
        ScheduledFuture<?> task = scheduler.schedule(() -> {
            finalizeSosDispatch(incidentId);
        }, 15, TimeUnit.SECONDS);

        activeSosTasks.put(incidentId, task);
        return savedIncident;
    }

    // 15 sec complete hone ke baad final dispatch state set karne ke liye
    private void finalizeSosDispatch(String id) {
        activeSosTasks.remove(id);
        sosRepository.findById(id).ifPresent(incident -> {
            if ("PENDING_DISPATCH".equals(incident.getStatus())) {
                incident.setStatus("ACTIVE");
                sosRepository.save(incident);
                System.out.println("🔴 CRITICAL: 15 seconds passed! SOS is now officially ACTIVE and DISPATCHED for ID: " + id);
                // Yahan par aap SMS/FCM notification trigger kar sakte hain
            }
        });
    }

    // SOS cancel karne ke liye (15 seconds window ke andar)
    public SosIncident cancelSos(String id) {
        // Sabse pehle running timer ko cancel karo agar wo active hai
        ScheduledFuture<?> task = activeSosTasks.remove(id);
        if (task != null && !task.isDone()) {
            task.cancel(false);
            System.out.println("✅ Timer aborted for SOS ID: " + id);
        }

        return sosRepository.findById(id).map(incident -> {
            if ("PENDING_DISPATCH".equals(incident.getStatus()) || "ACTIVE".equals(incident.getStatus())) {
                incident.setStatus("CANCELLED");
                return sosRepository.save(incident);
            }
            return incident;
        }).orElse(null);
    }

    // Silent SOS Trigger karne ke liye (Yeh instant rahega bina delay ke)
    public SosIncident triggerSilentSos(String userId) {
        SosIncident incident = new SosIncident(userId);
        incident.setSilent(true);
        incident.setStatus("SILENT_ACTIVE");
        return sosRepository.save(incident);
    }

    // User ki SOS history fetch karne ke liye
    public List<SosIncident> getSosHistory(String userId) {
        return sosRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}