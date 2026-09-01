package com.devcollab.devcollab.service;

import com.devcollab.devcollab.dto.request.InviteMemberRequest;
import com.devcollab.devcollab.dto.request.WorkspaceRequest;
import com.devcollab.devcollab.dto.response.WorkspaceMemberResponse;
import com.devcollab.devcollab.dto.response.WorkspaceResponse;

import java.util.List;

public interface WorkspaceService {
    WorkspaceResponse createWorkspace(WorkspaceRequest request);
    List<WorkspaceResponse> getMyWorkspaces();
    WorkspaceResponse getWorkspace(String workspaceId);
    WorkspaceResponse updateWorkspace(String workspaceId, WorkspaceRequest request);
    void deleteWorkspace(String workspaceId);
    WorkspaceMemberResponse inviteMember(String workspaceId, InviteMemberRequest request);
    List<WorkspaceMemberResponse> getMembers(String workspaceId);
    void removeMember(String workspaceId, String userId);
}