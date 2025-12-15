package com.safesite.controller;

import com.safesite.entity.Role;
import com.safesite.entity.User;
import com.safesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/chefs")
    public ResponseEntity<List<User>> getAllChefs() {
        List<User> chefs = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CHEF)
                .toList();
        return ResponseEntity.ok(chefs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String fullName = request.get("fullName");
        String roleStr = request.getOrDefault("role", "CHEF");

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().build();
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password != null ? password : "pass"))
                .fullName(fullName)
                .role(Role.valueOf(roleStr.toUpperCase()))
                .build();

        user = userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return userRepository.findById(id)
                .map(user -> {
                    if (request.containsKey("fullName")) {
                        user.setFullName(request.get("fullName"));
                    }
                    if (request.containsKey("email")) {
                        user.setEmail(request.get("email"));
                    }
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode("pass"));
                    userRepository.save(user);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email/{email}/sites")
    public ResponseEntity<List<Map<String, Object>>> getSitesByUserEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    // Get all sites from all assigned PROJECTS
                    List<Map<String, Object>> sites = user.getAssignedProjects().stream()
                            .flatMap(project -> project.getSites().stream())
                            .map(site -> {
                                Map<String, Object> siteMap = new java.util.HashMap<>();
                                siteMap.put("id", site.getId());
                                siteMap.put("name", site.getName());
                                siteMap.put("type", site.getType());
                                siteMap.put("projectId", site.getProject() != null ? site.getProject().getId() : null);
                                siteMap.put("projectName",
                                        site.getProject() != null ? site.getProject().getName() : null);
                                return siteMap;
                            })
                            .collect(java.util.stream.Collectors.toList());
                    return ResponseEntity.ok(sites);
                })
                .orElse(ResponseEntity.ok(java.util.Collections.emptyList()));
    }

    @GetMapping("/by-email/{email}/projects")
    public ResponseEntity<List<Map<String, Object>>> getProjectsByUserEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    List<Map<String, Object>> projects = user.getAssignedProjects().stream()
                            .map(project -> {
                                Map<String, Object> projectMap = new java.util.HashMap<>();
                                projectMap.put("id", project.getId());
                                projectMap.put("name", project.getName());
                                projectMap.put("location", project.getLocation());
                                projectMap.put("sitesCount", project.getSites().size());
                                return projectMap;
                            })
                            .collect(java.util.stream.Collectors.toList());
                    return ResponseEntity.ok(projects);
                })
                .orElse(ResponseEntity.ok(java.util.Collections.emptyList()));
    }
}
