package com.sentinelx.backend.controller;

import com.sentinelx.backend.dto.ScanRequest;
import com.sentinelx.backend.dto.ScanResponse;
import com.sentinelx.backend.service.ScanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails) {

        ScanResponse response = scanService.scanUrl(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ScanResponse>> getScanHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ScanResponse> history = scanService.getUserScans(userDetails.getUsername());
        return ResponseEntity.ok(history);
    }
}