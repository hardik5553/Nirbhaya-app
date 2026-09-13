package com.SIH.Women.Safety.Device.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SIH.Women.Safety.Device.Repository.UserRepository;
import com.SIH.Women.Safety.Device.model.User;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request) {
        User user = findUser(request);
        if (user == null) return ResponseEntity.status(404).body(Map.of("message", "User not found."));
        if (request.get("username") != null) user.setUsername(request.get("username"));
        if (request.get("phoneNumber") != null) user.setPhoneNumber(request.get("phoneNumber"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully.", "username", user.getUsername(),
                "phoneNumber", user.getPhoneNumber() == null ? "" : user.getPhoneNumber()));
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        User user = findUser(request);
        if (user == null || !passwordEncoder.matches(request.getOrDefault("oldPassword", ""), user.getPassword())) {
            return ResponseEntity.status(403).body(Map.of("message", "Incorrect current password."));
        }
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "New password must be at least 6 characters."));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }

    private User findUser(Map<String, String> request) {
        String email = request.get("email");
        return email == null ? null : userRepository.findByEmail(email).orElse(null);
    }
}