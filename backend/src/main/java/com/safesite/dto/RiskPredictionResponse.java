package com.safesite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for ML Service risk prediction response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskPredictionResponse {
    private Integer riskScore;
    private String riskLevel;
    private List<String> recommendations;
}
