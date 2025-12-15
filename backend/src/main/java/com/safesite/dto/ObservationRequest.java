package com.safesite.dto;

import lombok.Data;

@Data
public class ObservationRequest {
    private Long siteId;
    private Double temperature;
    private Double humidity;
    private Double epiCompliance;
    private Double fatigue;
    private Double workingHours;
    private Integer workersCount;
    private Boolean hazardousMaterials;
    private String weatherConditions;
    private String notes;
}
