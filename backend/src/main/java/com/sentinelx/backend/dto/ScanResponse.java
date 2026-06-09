package com.sentinelx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScanResponse {
    private Long id;
    private String url;
    private Integer riskScore;
    private String riskLevel;
    private Integer maliciousCount;
    private Integer totalEngines;
    private String aiExplanation;
    private String scannedBy;
    private LocalDateTime scannedAt;
}
