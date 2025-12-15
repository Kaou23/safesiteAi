package com.safesite.service;

import com.safesite.dto.*;
import com.safesite.entity.Observation;
import com.safesite.entity.Site;
import com.safesite.entity.User;
import com.safesite.repository.ObservationRepository;
import com.safesite.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObservationService {

    private final ObservationRepository observationRepository;
    private final SiteRepository siteRepository;
    private final RiskAnalysisService riskAnalysisService;

    @Transactional
    public ObservationResponse createObservation(ObservationRequest request, User user) {
        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new RuntimeException("Site not found"));

        // Call ML service for risk analysis
        RiskPredictionResponse riskResult = riskAnalysisService.analyzeRisk(
                request.getTemperature(),
                request.getHumidity(),
                request.getEpiCompliance(),
                request.getFatigue(),
                request.getWorkingHours(),
                request.getWorkersCount(),
                request.getHazardousMaterials(),
                request.getWeatherConditions());

        Observation observation = Observation.builder()
                .site(site)
                .createdBy(user)
                .temperature(request.getTemperature())
                .humidity(request.getHumidity())
                .epiCompliance(request.getEpiCompliance())
                .fatigue(request.getFatigue())
                .workingHours(request.getWorkingHours())
                .workersCount(request.getWorkersCount())
                .hazardousMaterials(request.getHazardousMaterials())
                .weatherConditions(request.getWeatherConditions())
                .notes(request.getNotes())
                .riskScore(riskResult.getRiskScore())
                .riskLevel(riskResult.getRiskLevel())
                .recommendations(String.join("|||", riskResult.getRecommendations()))
                .build();

        observation = observationRepository.save(observation);

        return mapToResponse(observation, riskResult.getRecommendations());
    }

    public List<ObservationResponse> getAllObservations() {
        return observationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ObservationResponse> getObservationsBySite(Long siteId) {
        return observationRepository.findBySiteId(siteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ObservationResponse getObservationById(Long id) {
        Observation observation = observationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Observation not found"));
        return mapToResponse(observation);
    }

    private ObservationResponse mapToResponse(Observation observation) {
        List<String> recommendations = observation.getRecommendations() != null
                ? Arrays.asList(observation.getRecommendations().split("\\|\\|\\|"))
                : List.of();
        return mapToResponse(observation, recommendations);
    }

    private ObservationResponse mapToResponse(Observation observation, List<String> recommendations) {
        return ObservationResponse.builder()
                .id(observation.getId())
                .siteId(observation.getSite().getId())
                .siteName(observation.getSite().getName())
                .temperature(observation.getTemperature())
                .humidity(observation.getHumidity())
                .epiCompliance(observation.getEpiCompliance())
                .fatigue(observation.getFatigue())
                .workingHours(observation.getWorkingHours())
                .workersCount(observation.getWorkersCount())
                .hazardousMaterials(observation.getHazardousMaterials())
                .weatherConditions(observation.getWeatherConditions())
                .notes(observation.getNotes())
                .riskScore(observation.getRiskScore())
                .riskLevel(observation.getRiskLevel())
                .recommendations(recommendations)
                .createdAt(observation.getCreatedAt())
                .build();
    }
}
