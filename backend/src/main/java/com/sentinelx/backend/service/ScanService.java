package com.sentinelx.backend.service;

import com.sentinelx.backend.dto.ScanRequest;
import com.sentinelx.backend.dto.ScanResponse;
import com.sentinelx.backend.model.Scan;
import com.sentinelx.backend.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanRepository scanRepository;
    private final VirusTotalService virusTotalService;

    public ScanResponse scanUrl(ScanRequest request, String userEmail) {
        // Step 1 — Call VirusTotal
        Map<String, Object> vtResult = virusTotalService.scanUrl(request.getUrl());

        // Step 2 — Extract stats from result
        Map<String, Object> data = (Map<String, Object>) vtResult.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        Map<String, Object> stats = (Map<String, Object>) attributes.get("stats");

        int malicious = (int) stats.get("malicious");
        int totalEngines = malicious
                + (int) stats.get("harmless")
                + (int) stats.get("suspicious")
                + (int) stats.get("undetected");

        // Step 3 — Calculate risk
        int riskScore = virusTotalService.calculateRiskScore(malicious, totalEngines);
        String riskLevel = virusTotalService.getRiskLevel(riskScore);

        // Step 4 — Save to database
        Scan scan = Scan.builder()
                .url(request.getUrl())
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .maliciousCount(malicious)
                .totalEngines(totalEngines)
                .aiExplanation("Scanned by " + totalEngines + " engines. " + malicious + " flagged as malicious.")
                .scannedBy(userEmail)
                .build();

        Scan saved = scanRepository.save(scan);

        // Step 5 — Return response
        return mapToResponse(saved);
    }

    public List<ScanResponse> getUserScans(String userEmail) {
        return scanRepository.findByScannedByOrderByScannedAtDesc(userEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ScanResponse mapToResponse(Scan scan) {
        return ScanResponse.builder()
                .id(scan.getId())
                .url(scan.getUrl())
                .riskScore(scan.getRiskScore())
                .riskLevel(scan.getRiskLevel())
                .maliciousCount(scan.getMaliciousCount())
                .totalEngines(scan.getTotalEngines())
                .aiExplanation(scan.getAiExplanation())
                .scannedBy(scan.getScannedBy())
                .scannedAt(scan.getScannedAt())
                .build();
    }
}