package com.devcollab.devcollab.dto.response;

import com.devcollab.devcollab.entity.ProjectStatus;
import com.devcollab.devcollab.entity.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private String id;
    private String name;
    private String description;
    private String workspaceId;
    private String createdByName;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private LocalDateTime createdAt;
}