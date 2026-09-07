package com.SIH.Women.Safety.Device.controller;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.SIH.Women.Safety.Device.Repository.UserRepository;
import com.SIH.Women.Safety.Device.model.User;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void registerHashesPasswordAndReturnsNumericId() {
        authController = new AuthController();
        injectDependencies();

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("secret123");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId("mongo-user-1");
            return saved;
        });

        ResponseEntity<?> response = authController.registerUser(user);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(passwordEncoder.matches("secret123", user.getPassword()));
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("uniqueNumericId"));
    }

    @Test
    void loginAcceptsCorrectPasswordAndReturnsUserId() {
        authController = new AuthController();
        injectDependencies();

        User storedUser = new User();
        storedUser.setId("mongo-user-1");
        storedUser.setEmail("user@example.com");
        storedUser.setPassword(passwordEncoder.encode("secret123"));
        storedUser.setUsername("Test User");
        when(userRepository.findByEmail(storedUser.getEmail())).thenReturn(Optional.of(storedUser));

        User request = new User();
        request.setEmail(storedUser.getEmail());
        request.setPassword("secret123");

        ResponseEntity<?> response = authController.loginUser(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("mongo-user-1", ((Map<?, ?>) response.getBody()).get("userId"));
    }

    @Test
    void recoveryCheckAcceptsRegisteredEmail() {
        authController = new AuthController();
        injectDependencies();

        User storedUser = new User();
        storedUser.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(storedUser));

        ResponseEntity<?> response = authController.recoveryCheck("USER@EXAMPLE.COM");

        assertEquals(200, response.getStatusCode().value());
    }

    private void injectDependencies() {
        try {
            var repositoryField = AuthController.class.getDeclaredField("userRepository");
            repositoryField.setAccessible(true);
            repositoryField.set(authController, userRepository);
            var encoderField = AuthController.class.getDeclaredField("passwordEncoder");
            encoderField.setAccessible(true);
            encoderField.set(authController, passwordEncoder);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
