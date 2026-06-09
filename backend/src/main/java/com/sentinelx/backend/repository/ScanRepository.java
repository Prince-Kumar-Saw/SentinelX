package com.sentinelx.backend.repository;

import com.sentinelx.backend.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {
    List<Scan> findByScannedByOrderByScannedAtDesc(String email);
    Long countByScannedBy(String email);
}