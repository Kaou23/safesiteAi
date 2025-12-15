package com.safesite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObservationResponse {
    private Long id;
    private Long siteId;
    private String siteName;
    private Double temperature;
    private Double humidity;
    private Double epiCompliance;
    private Double fatigue;
    private Double workingHours;
    private Integer workersCount;
    private Boolean hazardousMaterials;
    private String weatherConditions;
    private String notes;
    private Integer riskScore;
    private String riskLevel;
    private List<String> recommendations;
    private LocalDateTime createdAt;
}
