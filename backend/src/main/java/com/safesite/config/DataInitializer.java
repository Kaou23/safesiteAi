package com.safesite.config;

import com.safesite.entity.Project;
import com.safesite.entity.Role;
import com.safesite.entity.Site;
import com.safesite.entity.User;
import com.safesite.repository.ProjectRepository;
import com.safesite.repository.SiteRepository;
import com.safesite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final ProjectRepository projectRepository;
        private final SiteRepository siteRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                // Always ensure admin exists
                if (!userRepository.existsByEmail("admin@safesite.ai")) {
                        User admin = User.builder()
                                        .email("admin@safesite.ai")
                                        .password(passwordEncoder.encode("pass"))
                                        .role(Role.ADMIN)
                                        .fullName("Administrateur SafeSite")
                                        .build();
                        userRepository.save(admin);
                        log.info("Created admin user: admin@safesite.ai / pass");
                } else {
                        log.info("Admin user already exists");
                }

                // Always ensure chef exists
                if (!userRepository.existsByEmail("chef@safesite.ai")) {
                        User chef = User.builder()
                                        .email("chef@safesite.ai")
                                        .password(passwordEncoder.encode("pass"))
                                        .role(Role.CHEF)
                                        .fullName("Chef de Chantier")
                                        .build();
                        userRepository.save(chef);
                        log.info("Created chef user: chef@safesite.ai / pass");
                } else {
                        log.info("Chef user already exists");
                }

                // Create demo project if needed
                if (projectRepository.count() == 0) {
                        log.info("Initializing demo data...");

                        Project project = Project.builder()
                                        .name("Grand Paris Express")
                                        .location("Île-de-France, Paris")
                                        .build();
                        project = projectRepository.save(project);
                        log.info("Created project: Grand Paris Express");

                        Site site1 = Site.builder()
                                        .name("Station Saint-Denis Pleyel")
                                        .type("Gare souterraine")
                                        .project(project)
                                        .build();
                        siteRepository.save(site1);

                        Site site2 = Site.builder()
                                        .name("Tunnel Ligne 16 - Section Nord")
                                        .type("Tunnel TBM")
                                        .project(project)
                                        .build();
                        siteRepository.save(site2);

                        log.info("Created 2 sites for Grand Paris Express");
                        log.info("Demo data initialization complete!");
                }
        }
}
