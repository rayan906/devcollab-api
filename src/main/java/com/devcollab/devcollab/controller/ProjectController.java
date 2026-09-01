package com.devcollab.devcollab.controller;

import com.devcollab.devcollab.dto.request.ProjectRequest;
import com.devcollab.devcollab.dto.response.ApiResponse;
import com.devcollab.devcollab.dto.response.ProjectResponse;
import com.devcollab.devcollab.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @PathVariable String workspaceId,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created", projectService.createProject(workspaceId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(
            @PathVariable String workspaceId) {
        return ResponseEntity.ok(ApiResponse.success("Projects fetched", projectService.getProjects(workspaceId)));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable String workspaceId,
            @PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.success("Project fetched", projectService.getProject(workspaceId, projectId)));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Project updated", projectService.updateProject(workspaceId, projectId, request)));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable String workspaceId,
            @PathVariable String projectId) {
        projectService.deleteProject(workspaceId, projectId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted", null));
    }
}