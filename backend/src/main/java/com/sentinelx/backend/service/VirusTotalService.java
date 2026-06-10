package com.sentinelx.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-apikey", apiKey);

            // Step 1 — First check if URL already exists in VirusTotal
            String urlId = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(url.getBytes(StandardCharsets.UTF_8));

            try {
                HttpEntity<Void> checkRequest = new HttpEntity<>(headers);
                ResponseEntity<Map> checkResponse = restTemplate.exchange(
                    apiUrl + "/urls/" + urlId,
                    HttpMethod.GET,
                    checkRequest,
                    Map.class
                );

                if (checkResponse.getStatusCode() == HttpStatus.OK) {
                    return checkResponse.getBody();
                }
            } catch (Exception ignored) {
                // URL not in database yet — submit it
            }

            // Step 2 — Submit URL to VirusTotal
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
            HttpEntity<String> submitRequest = new HttpEntity<>("url=" + encodedUrl, headers);

            ResponseEntity<Map> submitResponse = restTemplate.exchange(
                apiUrl + "/urls",
                HttpMethod.POST,
                submitRequest,
                Map.class
            );

            // Step 3 — Get scan ID
            Map<String, Object> submitData = (Map<String, Object>) submitResponse.getBody().get("data");
            String scanId = (String) submitData.get("id");

            // Step 4 — Poll for results
            HttpHeaders getHeaders = new HttpHeaders();
            getHeaders.set("x-apikey", apiKey);
            HttpEntity<Void> getRequest = new HttpEntity<>(getHeaders);

            for (int i = 0; i < 10; i++) {
                Thread.sleep(3000);

                ResponseEntity<Map> resultResponse = restTemplate.exchange(
                    apiUrl + "/analyses/" + scanId,
                    HttpMethod.GET,
                    getRequest,
                    Map.class
                );

                Map<String, Object> body = resultResponse.getBody();
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
                String status = (String) attributes.get("status");

                if ("completed".equals(status)) {
                    return body;
                }
            }

            // Step 5 — Return last result
            ResponseEntity<Map> finalResponse = restTemplate.exchange(
                apiUrl + "/analyses/" + scanId,
                HttpMethod.GET,
                getRequest,
                Map.class
            );
            return finalResponse.getBody();

        } catch (Exception e) {
            throw new RuntimeException("VirusTotal scan failed: " + e.getMessage());
        }
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