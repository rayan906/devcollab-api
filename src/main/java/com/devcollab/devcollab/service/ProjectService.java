package com.devcollab.devcollab.service;

import com.devcollab.devcollab.dto.request.ProjectRequest;
import com.devcollab.devcollab.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(String workspaceId, ProjectRequest request);
    List<ProjectResponse> getProjects(String workspaceId);
    ProjectResponse getProject(String workspaceId, String projectId);
    ProjectResponse updateProject(String workspaceId, String projectId, ProjectRequest request);
    void deleteProject(String workspaceId, String projectId);
}