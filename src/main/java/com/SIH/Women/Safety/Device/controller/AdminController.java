package com.SIH.Women.Safety.Device.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.Repository.UserRepository;
import com.SIH.Women.Safety.Device.model.User;
import com.SIH.Women.Safety.Device.service.AdminSessionService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AdminSessionService adminSessionService;

    public AdminController(UserRepository userRepository, AdminSessionService adminSessionService) {
        this.userRepository = userRepository;
        this.adminSessionService = adminSessionService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> users(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail) {
        if (!adminSessionService.isValid(adminToken, adminEmail)) {
            return ResponseEntity.status(401).body(Map.of("message", "Admin authentication required."));
        }
        User admin = userRepository.findByEmail(adminEmail).orElse(null);
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(403).body(Map.of("message", "Administrator access required."));
        }
        List<Map<String, Object>> users = userRepository.findAll().stream().map(this::safeUser).toList();
        return ResponseEntity.ok(users);
    }

    private Map<String, Object> safeUser(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername() == null ? "" : user.getUsername(),
                "email", user.getEmail(),
                "verified", user.isVerified(),
                "role", user.getRole() == null ? "USER" : user.getRole());
    }
}