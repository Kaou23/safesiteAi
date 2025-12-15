package com.safesite.controller;

import com.safesite.dto.SiteResponse;
import com.safesite.entity.Site;
import com.safesite.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sites")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SiteController {

    private final SiteRepository siteRepository;

    @GetMapping
    public ResponseEntity<List<SiteResponse>> getAllSites() {
        List<SiteResponse> sites = siteRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getSiteById(@PathVariable Long id) {
        return siteRepository.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        if (!siteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        siteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiteResponse> updateSite(@PathVariable Long id,
            @RequestBody java.util.Map<String, String> request) {
        return siteRepository.findById(id)
                .map(site -> {
                    if (request.containsKey("name")) {
                        site.setName(request.get("name"));
                    }
                    if (request.containsKey("type")) {
                        site.setType(request.get("type"));
                    }
                    siteRepository.save(site);
                    return ResponseEntity.ok(mapToResponse(site));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private SiteResponse mapToResponse(Site site) {
        return SiteResponse.builder()
                .id(site.getId())
                .name(site.getName())
                .type(site.getType())
                .projectId(site.getProject() != null ? site.getProject().getId() : null)
                .projectName(site.getProject() != null ? site.getProject().getName() : null)
                .build();
    }
}
