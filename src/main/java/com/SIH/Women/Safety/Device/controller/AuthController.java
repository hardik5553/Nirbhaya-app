package com.SIH.Women.Safety.Device.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.Repository.UserRepository;
import com.SIH.Women.Safety.Device.model.User;
import com.SIH.Women.Safety.Device.service.AdminSessionService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @org.springframework.beans.factory.annotation.Value("${app.admin.registration-key:}")
    private String adminRegistrationKey = "";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminSessionService adminSessionService = new AdminSessionService();

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getEmail() == null || user.getPassword() == null || user.getPassword().isBlank()) {
            response.put("message", "Email and password are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if email already exists
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("message", "Email is already registered!");
            return ResponseEntity.badRequest().body(response);
        }

        // Generate a 6-digit unique numeric ID if not present
        if (user.getUniqueNumericId() == null || user.getUniqueNumericId().isEmpty()) {
            String randomId = String.valueOf((int)(Math.random() * 900000) + 100000);
            user.setUniqueNumericId(randomId);
        }

        // Set default profile flags if null
        user.setVerified(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getDefaultRiskLevel() == null) {
            user.setDefaultRiskLevel("GREEN");
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            if (adminRegistrationKey.isBlank() || !adminRegistrationKey.equals(user.getAdminRegistrationKey())) {
                response.put("message", "Wrong administrator key. Please continue as a user; you do not have admin access.");
                return ResponseEntity.status(403).body(response);
            }
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        // Save user through the backend persistence layer.
        userRepository.save(user);
        
        response.put("message", "Account created successfully!");
        response.put("uniqueNumericId", user.getUniqueNumericId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Map<String, Object> response = new HashMap<>();

        // Support login via Email or Username
        Optional<User> userOpt = Optional.empty();
        
        if (loginRequest.getEmail() != null) {
            userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByUsername(loginRequest.getEmail());
            }
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Match password
            if (user.getPassword() != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                response.put("message", "Login successful!");
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("phoneNumber", user.getPhoneNumber());
                response.put("uniqueNumericId", user.getUniqueNumericId());
                response.put("role", user.getRole() == null ? "USER" : user.getRole());
                return ResponseEntity.ok(response);
            }
        }

        response.put("message", "Invalid email/username or password!");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/recovery-check")
    public ResponseEntity<?> recoveryCheck(@RequestParam String email) {
        if (email.isBlank() || userRepository.findByEmail(email.trim().toLowerCase()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Account is eligible for password recovery."));
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody User loginRequest) {
        if (adminRegistrationKey.isBlank()
                || loginRequest.getAdminRegistrationKey() == null
                || !adminRegistrationKey.equals(loginRequest.getAdminRegistrationKey())) {
            return ResponseEntity.status(403).body(Map.of(
                    "message", "Wrong administrator key. Please continue as a user; you do not have admin access."));
        }

        Optional<User> userOpt = loginRequest.getEmail() == null
                ? Optional.empty()
                : userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean adminRole = "ADMIN".equalsIgnoreCase(user.getRole());
                if (adminRole
                    && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.ok(Map.of(
                        "message", "Admin login successful!",
                        "adminToken", adminSessionService.create(user.getEmail()),
                        "userId", user.getId(),
                        "username", user.getUsername() == null ? "Administrator" : user.getUsername(),
                        "email", user.getEmail(),
                        "role", "ADMIN"));
            }
        }
        return ResponseEntity.status(403).body(Map.of("message", "Administrator credentials are invalid."));
    }
}