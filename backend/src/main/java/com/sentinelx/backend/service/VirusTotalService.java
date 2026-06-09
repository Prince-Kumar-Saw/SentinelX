package com.sentinelx.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class VirusTotalService {

    @Value("${virustotal.api.key}")
    private String apiKey;

    @Value("${virustotal.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> scanUrl(String url) {
        // Step 1 — Submit URL to VirusTotal
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apikey", apiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> submitRequest = new HttpEntity<>("url=" + url, headers);
        ResponseEntity<Map> submitResponse = restTemplate.exchange(
            apiUrl + "/urls",
            HttpMethod.POST,
            submitRequest,
            Map.class
        );

        // Step 2 — Get the scan ID from response
        Map<String, Object> submitData = (Map<String, Object>) submitResponse.getBody().get("data");
        String scanId = (String) submitData.get("id");

        // Step 3 — Fetch the scan result using the ID
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<Map> resultResponse = restTemplate.exchange(
            apiUrl + "/analyses/" + scanId,
            HttpMethod.GET,
            getRequest,
            Map.class
        );

        return resultResponse.getBody();
    }

    public int calculateRiskScore(int malicious, int total) {
        if (total == 0) return 0;
        return (int) ((malicious * 100.0) / total);
    }

    public String getRiskLevel(int riskScore) {
        if (riskScore == 0) return "SAFE";
        if (riskScore <= 10) return "LOW";
        if (riskScore <= 40) return "MEDIUM";
        return "HIGH";
    }
}