package com.safesite.service;

import com.safesite.dto.ProjectResponse;
import com.safesite.dto.SiteResponse;
import com.safesite.entity.Project;
import com.safesite.entity.Role;
import com.safesite.entity.Site;
import com.safesite.entity.User;
import com.safesite.repository.ProjectRepository;
import com.safesite.repository.SiteRepository;
import com.safesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

        private final ProjectRepository projectRepository;
        private final SiteRepository siteRepository;
        private final UserRepository userRepository;

        public List<ProjectResponse> getAllProjects() {
                return projectRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public ProjectResponse getProjectById(Long id) {
                Project project = projectRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Project not found"));
                return mapToResponse(project);
        }

        public ProjectResponse createProject(String name, String location) {
                Project project = Project.builder()
                                .name(name)
                                .location(location)
                                .build();
                project = projectRepository.save(project);
                return mapToResponse(project);
        }

        public List<SiteResponse> getSitesByProject(Long projectId) {
                return siteRepository.findByProjectId(projectId).stream()
                                .map(this::mapSiteToResponse)
                                .collect(Collectors.toList());
        }

        public SiteResponse createSite(Long projectId, String name, String type) {
                Project project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new RuntimeException("Project not found"));

                Site site = Site.builder()
                                .name(name)
                                .type(type)
                                .project(project)
                                .build();
                site = siteRepository.save(site);
                return mapSiteToResponse(site);
        }

        public void deleteProject(Long id) {
                Project project = projectRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Project not found"));
                projectRepository.delete(project);
        }

        public List<Map<String, Object>> getChefsByProject(Long projectId) {
                List<User> allChefs = userRepository.findByRole(Role.CHEF);
                Project project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new RuntimeException("Project not found"));

                List<Map<String, Object>> result = new ArrayList<>();
                for (User chef : allChefs) {
                        Map<String, Object> chefMap = new HashMap<>();
                        chefMap.put("id", chef.getId());
                        chefMap.put("fullName", chef.getFullName());
                        chefMap.put("email", chef.getEmail());
                        chefMap.put("assigned", chef.getAssignedProjects().contains(project));
                        result.add(chefMap);
                }
                return result;
        }

        @Transactional
        public void assignChefToProject(Long projectId, Long chefId) {
                Project project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new RuntimeException("Project not found"));
                User chef = userRepository.findById(chefId)
                                .orElseThrow(() -> new RuntimeException("Chef not found"));

                chef.getAssignedProjects().add(project);
                userRepository.save(chef);
        }

        @Transactional
        public void removeChefFromProject(Long projectId, Long chefId) {
                Project project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new RuntimeException("Project not found"));
                User chef = userRepository.findById(chefId)
                                .orElseThrow(() -> new RuntimeException("Chef not found"));

                chef.getAssignedProjects().remove(project);
                userRepository.save(chef);
        }

        private ProjectResponse mapToResponse(Project project) {
                List<SiteResponse> sites = project.getSites().stream()
                                .map(this::mapSiteToResponse)
                                .collect(Collectors.toList());

                return ProjectResponse.builder()
                                .id(project.getId())
                                .name(project.getName())
                                .location(project.getLocation())
                                .sites(sites)
                                .build();
        }

        private SiteResponse mapSiteToResponse(Site site) {
                return SiteResponse.builder()
                                .id(site.getId())
                                .name(site.getName())
                                .type(site.getType())
                                .projectId(site.getProject().getId())
                                .build();
        }
}
