package com.devcollab.devcollab.service.impl;

import com.devcollab.devcollab.dto.request.ProjectRequest;
import com.devcollab.devcollab.dto.response.ProjectResponse;
import com.devcollab.devcollab.entity.*;
import com.devcollab.devcollab.repository.*;
import com.devcollab.devcollab.service.ProjectService;
import com.devcollab.devcollab.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public ProjectResponse createProject(String workspaceId, ProjectRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Workspace workspace = getWorkspaceAndVerifyMember(workspaceId);
        verifyAdminOrOwner(workspaceId);

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .workspace(workspace)
                .createdBy(currentUser)
                .visibility(request.getVisibility() != null ?
                        request.getVisibility() : ProjectVisibility.PUBLIC)
                .build();

        return mapToResponse(projectRepository.save(project));
    }

    @Override
    public List<ProjectResponse> getProjects(String workspaceId) {
        getWorkspaceAndVerifyMember(workspaceId);
        return projectRepository.findAllByWorkspaceId(workspaceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProjectResponse getProject(String workspaceId, String projectId) {
        getWorkspaceAndVerifyMember(workspaceId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return mapToResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(String workspaceId, String projectId, ProjectRequest request) {
        getWorkspaceAndVerifyMember(workspaceId);
        verifyAdminOrOwner(workspaceId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        if (request.getVisibility() != null) {
            project.setVisibility(request.getVisibility());
        }

        return mapToResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void deleteProject(String workspaceId, String projectId) {
        getWorkspaceAndVerifyMember(workspaceId);
        verifyAdminOrOwner(workspaceId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
    }

    // ── Helper methods ──────────────────────────────────────────

    private Workspace getWorkspaceAndVerifyMember(String workspaceId) {
        String currentUserId = securityUtil.getCurrentUserId();
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));
        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, currentUserId)) {
            throw new RuntimeException("You are not a member of this workspace");
        }
        return workspace;
    }

    private void verifyAdminOrOwner(String workspaceId) {
        String currentUserId = securityUtil.getCurrentUserId();
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        if (member.getRole() == WorkspaceRole.MEMBER || member.getRole() == WorkspaceRole.VIEWER) {
            throw new RuntimeException("You don't have permission to perform this action");
        }
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .workspaceId(project.getWorkspace().getId())
                .createdByName(project.getCreatedBy().getName())
                .status(project.getStatus())
                .visibility(project.getVisibility())
                .createdAt(project.getCreatedAt())
                .build();
    }
}