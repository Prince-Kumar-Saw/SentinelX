package com.sentinelx.backend.service;

import com.sentinelx.backend.dto.AuthResponse;
import com.sentinelx.backend.dto.LoginRequest;
import com.sentinelx.backend.dto.RegisterRequest;
import com.sentinelx.backend.model.User;
import com.sentinelx.backend.repository.UserRepository;
import com.sentinelx.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Register a new user
    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.VIEWER)
                .build();

        // Save to database
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                "Registration successful!"
        );
    }

    // Login existing user
    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                "Login successful!"
        );
    }
}