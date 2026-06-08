package com.sentinelx.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    // Public endpoint - no token needed
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "SentinelX");
        response.put("message", "Cyber Threat Intelligence Platform is running!");
        return ResponseEntity.ok(response);
    }

    // Protected endpoint - token required
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("email", authentication.getName());
        response.put("role", authentication.getAuthorities().toString());
        response.put("message", "You are authenticated!");
        return ResponseEntity.ok(response);
    }
}