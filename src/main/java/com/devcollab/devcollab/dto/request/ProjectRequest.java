package com.devcollab.devcollab.dto.request;

import com.devcollab.devcollab.entity.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
}