package com.safesite.controller;

import com.safesite.entity.Role;
import com.safesite.repository.ObservationRepository;
import com.safesite.repository.ProjectRepository;
import com.safesite.repository.SiteRepository;
import com.safesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StatsController {

    private final ProjectRepository projectRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final ObservationRepository observationRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        long totalProjects = projectRepository.count();
        long totalSites = siteRepository.count();
        long totalManagers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CHEF)
                .count();
        long totalObservations = observationRepository.count();

        // Count high risk observations as alerts
        long todayAlerts = observationRepository.findAll().stream()
                .filter(o -> "ÉLEVÉ".equals(o.getRiskLevel()) || "HIGH".equals(o.getRiskLevel()))
                .count();

        return ResponseEntity.ok(Map.of(
                "totalProjects", totalProjects,
                "totalSites", totalSites,
                "totalManagers", totalManagers,
                "totalObservations", totalObservations,
                "todayAlerts", todayAlerts));
    }
}
