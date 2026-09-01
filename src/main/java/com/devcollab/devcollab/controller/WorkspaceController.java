package com.devcollab.devcollab.controller;

import com.devcollab.devcollab.dto.request.InviteMemberRequest;
import com.devcollab.devcollab.dto.request.WorkspaceRequest;
import com.devcollab.devcollab.dto.response.ApiResponse;
import com.devcollab.devcollab.dto.response.WorkspaceMemberResponse;
import com.devcollab.devcollab.dto.response.WorkspaceResponse;
import com.devcollab.devcollab.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
            @Valid @RequestBody WorkspaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workspace created", workspaceService.createWorkspace(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getMyWorkspaces() {
        return ResponseEntity.ok(ApiResponse.success("Workspaces fetched", workspaceService.getMyWorkspaces()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> getWorkspace(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Workspace fetched", workspaceService.getWorkspace(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> updateWorkspace(
            @PathVariable String id,
            @Valid @RequestBody WorkspaceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Workspace updated", workspaceService.updateWorkspace(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.ok(ApiResponse.success("Workspace deleted", null));
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> inviteMember(
            @PathVariable String id,
            @Valid @RequestBody InviteMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member invited", workspaceService.inviteMember(id, request)));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceMemberResponse>>> getMembers(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Members fetched", workspaceService.getMembers(id)));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String id,
            @PathVariable String userId) {
        workspaceService.removeMember(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Member removed", null));
    }
}