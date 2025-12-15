package com.safesite.controller;

import com.safesite.dto.ProjectResponse;
import com.safesite.dto.SiteResponse;
import com.safesite.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(projectService.getProjectById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String location = request.get("location");
        return ResponseEntity.ok(projectService.createProject(name, location));
    }

    @GetMapping("/{id}/sites")
    public ResponseEntity<List<SiteResponse>> getProjectSites(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getSitesByProject(id));
    }

    @PostMapping("/{id}/sites")
    public ResponseEntity<SiteResponse> createSite(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String type = request.get("type");
        return ResponseEntity.ok(projectService.createSite(id, name, type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/chefs")
    public ResponseEntity<List<Map<String, Object>>> getProjectChefs(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getChefsByProject(id));
    }

    @PostMapping("/{id}/chefs/{chefId}")
    public ResponseEntity<Void> assignChefToProject(@PathVariable Long id, @PathVariable Long chefId) {
        try {
            projectService.assignChefToProject(id, chefId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/chefs/{chefId}")
    public ResponseEntity<Void> removeChefFromProject(@PathVariable Long id, @PathVariable Long chefId) {
        try {
            projectService.removeChefFromProject(id, chefId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
