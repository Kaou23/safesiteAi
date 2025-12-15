package com.safesite.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "observations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Observation data
    private Double temperature;
    private Double humidity;
    private Double epiCompliance; // Equipment Protection Individual compliance (%)
    private Double fatigue; // Fatigue level (1-10)
    private Double workingHours;
    private Integer workersCount;
    private Boolean hazardousMaterials;
    private String weatherConditions;
    private String notes;

    // Risk analysis result from ML service
    private Integer riskScore;
    private String riskLevel;

    @Column(length = 2000)
    private String recommendations;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    @JsonIgnore
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
