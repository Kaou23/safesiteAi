package com.safesite.service;

import com.safesite.dto.RiskPredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskAnalysisService {

    private final RestTemplate restTemplate;

    @Value("${app.ml-service.url}")
    private String mlServiceUrl;

    public RiskPredictionResponse analyzeRisk(Double temperature, Double humidity,
            Double epiCompliance, Double fatigue,
            Double workingHours, Integer workersCount,
            Boolean hazardousMaterials, String weatherConditions) {
        try {
            String url = mlServiceUrl + "/predict";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("temperature", temperature != null ? temperature : 25.0);
            requestBody.put("humidity", humidity != null ? humidity : 50.0);
            requestBody.put("epi_compliance", epiCompliance != null ? epiCompliance : 100.0);
            requestBody.put("fatigue", fatigue != null ? fatigue : 3.0);
            requestBody.put("working_hours", workingHours != null ? workingHours : 8.0);
            requestBody.put("workers_count", workersCount != null ? workersCount : 10);
            requestBody.put("hazardous_materials", hazardousMaterials != null ? hazardousMaterials : false);
            requestBody.put("weather_conditions", weatherConditions != null ? weatherConditions : "normal");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending risk analysis request to ML service: {}", requestBody);

            ResponseEntity<RiskPredictionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    RiskPredictionResponse.class);

            log.info("Received risk analysis response: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error calling ML service: {}", e.getMessage());

            // Fallback: Calculate risk locally if ML service is unavailable
            return calculateRiskLocally(epiCompliance, fatigue);
        }
    }

    private RiskPredictionResponse calculateRiskLocally(Double epiCompliance, Double fatigue) {
        log.warn("ML service unavailable, calculating risk locally");

        int riskScore = 0;
        String riskLevel;

        if ((epiCompliance != null && epiCompliance < 85) || (fatigue != null && fatigue > 6)) {
            riskScore = 75;
            riskLevel = "ÉLEVÉ";
        } else {
            riskScore = 25;
            riskLevel = "FAIBLE";
        }

        return new RiskPredictionResponse(
                riskScore,
                riskLevel,
                List.of("Analyse effectuée en mode local (service ML indisponible)"));
    }
}
