package com.sentinelx.backend.controller;

import com.sentinelx.backend.dto.ScanRequest;
import com.sentinelx.backend.dto.ScanResponse;
import com.sentinelx.backend.service.ScanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @PostMapping("/url")
    public ResponseEntity<ScanResponse> scanUrl(
            @Valid @RequestBody ScanRequest request,
            Authentication authentication) {

        ScanResponse response = scanService.scanUrl(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ScanResponse>> getScanHistory(
            Authentication authentication) {

        List<ScanResponse> history = scanService.getUserScans(authentication.getName());
        return ResponseEntity.ok(history);
    }
}