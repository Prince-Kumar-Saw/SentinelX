package com.sentinelx.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET = "SentinelXSecretKeyForJWTTokenGeneration2024!!";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generate token for a user
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email from token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Check if token is expired
    public boolean isTokenValid(String token) {
        try {
            return !extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Extract all claims from token
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}