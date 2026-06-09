package com.sentinelx.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scans")
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer riskScore;

    @Column(nullable = false)
    private String riskLevel;

    @Column(nullable = false)
    private Integer maliciousCount;

    @Column(nullable = false)
    private Integer totalEngines;

    @Column(length = 1000)
    private String aiExplanation;

    @Column(nullable = false)
    private String scannedBy;

    @Column(nullable = false)
    private LocalDateTime scannedAt;

    @PrePersist
    public void prePersist() {
        scannedAt = LocalDateTime.now();
    }
}