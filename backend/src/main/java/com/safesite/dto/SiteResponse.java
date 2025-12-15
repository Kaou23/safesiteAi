package com.safesite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteResponse {
    private Long id;
    private String name;
    private String type;
    private Long projectId;
    private String projectName;
}
