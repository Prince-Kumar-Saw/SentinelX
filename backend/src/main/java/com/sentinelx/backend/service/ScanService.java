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
        Map<String, Object> vtResult = virusTotalService.scanUrl(request.getUrl());

        int malicious = 0;
        int totalEngines = 0;

        try {
            Map<String, Object> data = (Map<String, Object>) vtResult.get("data");
            Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
            Map<String, Object> stats = (Map<String, Object>) attributes.get("stats");

            if (stats != null) {
                malicious = getIntValue(stats, "malicious");
                int harmless = getIntValue(stats, "harmless");
                int suspicious = getIntValue(stats, "suspicious");
                int undetected = getIntValue(stats, "undetected");
                totalEngines = malicious + harmless + suspicious + undetected;
            }

            if (totalEngines == 0) {
                Map<String, Object> lastStats = (Map<String, Object>) attributes.get("last_analysis_stats");
                if (lastStats != null) {
                    malicious = getIntValue(lastStats, "malicious");
                    int harmless = getIntValue(lastStats, "harmless");
                    int suspicious = getIntValue(lastStats, "suspicious");
                    int undetected = getIntValue(lastStats, "undetected");
                    totalEngines = malicious + harmless + suspicious + undetected;
                }
            }
        } catch (Exception e) {
            // Keep defaults if parsing fails
        }

        int riskScore = virusTotalService.calculateRiskScore(malicious, totalEngines);
        String riskLevel = virusTotalService.getRiskLevel(riskScore);

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
        return mapToResponse(saved);
    }

    public List<ScanResponse> getUserScans(String userEmail) {
        return scanRepository.findByScannedByOrderByScannedAtDesc(userEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteScan(Long id, String userEmail) {
        Scan scan = scanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scan not found!"));

        if (!scan.getScannedBy().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to delete this scan!");
        }

        scanRepository.delete(scan);
    }

    private int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Double) return ((Double) value).intValue();
        return 0;
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