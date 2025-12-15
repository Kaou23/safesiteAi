package com.safesite.controller;

import com.safesite.dto.ObservationRequest;
import com.safesite.dto.ObservationResponse;
import com.safesite.entity.User;
import com.safesite.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;

    @GetMapping
    public ResponseEntity<List<ObservationResponse>> getAllObservations() {
        return ResponseEntity.ok(observationService.getAllObservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObservationResponse> getObservation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(observationService.getObservationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<ObservationResponse>> getObservationsBySite(@PathVariable Long siteId) {
        return ResponseEntity.ok(observationService.getObservationsBySite(siteId));
    }

    @PostMapping
    public ResponseEntity<ObservationResponse> createObservation(
            @RequestBody ObservationRequest request,
            @AuthenticationPrincipal User user) {
        try {
            ObservationResponse response = observationService.createObservation(request, user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
