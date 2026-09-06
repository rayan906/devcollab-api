package com.devcollab.devcollab.service.impl;

import com.devcollab.devcollab.dto.request.InviteMemberRequest;
import com.devcollab.devcollab.dto.request.WorkspaceRequest;
import com.devcollab.devcollab.dto.response.WorkspaceMemberResponse;
import com.devcollab.devcollab.dto.response.WorkspaceResponse;
import com.devcollab.devcollab.entity.*;
import com.devcollab.devcollab.repository.*;
import com.devcollab.devcollab.service.EmailService;
import com.devcollab.devcollab.service.WorkspaceService;
import com.devcollab.devcollab.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;
    @Override
    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request) {
        User currentUser = securityUtil.getCurrentUser();

        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(currentUser)
                .build();

        Workspace saved = workspaceRepository.save(workspace);

        // Add creator as OWNER member
        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .workspace(saved)
                .user(currentUser)
                .role(WorkspaceRole.OWNER)
                .build();
        workspaceMemberRepository.save(ownerMember);

        return mapToResponse(saved);
    }

    @Override
    public List<WorkspaceResponse> getMyWorkspaces() {
        String userId = securityUtil.getCurrentUserId();
        return workspaceRepository.findAllByMemberUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "workspaces", key = "#workspaceId")
    public WorkspaceResponse getWorkspace(String workspaceId) {
        Workspace workspace = getWorkspaceAndVerifyMember(workspaceId);
        return mapToResponse(workspace);
    }

    @Override
    @Transactional
    @CacheEvict(value = "workspaces", key = "#workspaceId")
    public WorkspaceResponse updateWorkspace(String workspaceId, WorkspaceRequest request) {
        Workspace workspace = getWorkspaceAndVerifyMember(workspaceId);
        verifyAdminOrOwner(workspaceId);

        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());

        return mapToResponse(workspaceRepository.save(workspace));
    }

    @Override
    @Transactional
    @CacheEvict(value = "workspaces", key = "#workspaceId")
    public void deleteWorkspace(String workspaceId) {
        Workspace workspace = getWorkspaceAndVerifyMember(workspaceId);
        verifyOwner(workspaceId);
        workspaceRepository.delete(workspace);
    }

    @Override
    @Transactional
    public WorkspaceMemberResponse inviteMember(String workspaceId, InviteMemberRequest request) {
        getWorkspaceAndVerifyMember(workspaceId);
        verifyAdminOrOwner(workspaceId);

        if (request.getRole() == WorkspaceRole.OWNER) {
            throw new RuntimeException("Cannot assign OWNER role to invited members");
        }

        User invitedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, invitedUser.getId())) {
            throw new RuntimeException("User is already a member of this workspace");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();

        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .user(invitedUser)
                .role(request.getRole())
                .build();

        WorkspaceMember saved = workspaceMemberRepository.save(member);
        User currentUser = securityUtil.getCurrentUser();
        emailService.sendWorkspaceInviteEmail(
                invitedUser.getEmail(),
                invitedUser.getName(),
                workspace.getName(),
                currentUser.getName()
        );
        return mapMemberToResponse(saved);
    }

    @Override
    public List<WorkspaceMemberResponse> getMembers(String workspaceId) {
        getWorkspaceAndVerifyMember(workspaceId);
        return workspaceMemberRepository.findAllByWorkspaceId(workspaceId)
                .stream()
                .map(this::mapMemberToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeMember(String workspaceId, String userId) {
        getWorkspaceAndVerifyMember(workspaceId);
        verifyAdminOrOwner(workspaceId);

        String currentUserId = securityUtil.getCurrentUserId();
        if (currentUserId.equals(userId)) {
            throw new RuntimeException("You cannot remove yourself from the workspace");
        }

        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new RuntimeException("Cannot remove the workspace owner");
        }

        workspaceMemberRepository.delete(member);
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

    private void verifyOwner(String workspaceId) {
        String currentUserId = securityUtil.getCurrentUserId();
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        if (member.getRole() != WorkspaceRole.OWNER) {
            throw new RuntimeException("Only the workspace owner can perform this action");
        }
    }

    private WorkspaceResponse mapToResponse(Workspace workspace) {
        List<WorkspaceMemberResponse> members = workspaceMemberRepository
                .findAllByWorkspaceId(workspace.getId())
                .stream()
                .map(this::mapMemberToResponse)
                .toList();

        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .ownerId(workspace.getOwner().getId())
                .ownerName(workspace.getOwner().getName())
                .members(members)
                .createdAt(workspace.getCreatedAt())
                .build();
    }

    private WorkspaceMemberResponse mapMemberToResponse(WorkspaceMember member) {
        return WorkspaceMemberResponse.builder()
                .userId(member.getUser().getId())
                .name(member.getUser().getName())
                .email(member.getUser().getEmail())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}