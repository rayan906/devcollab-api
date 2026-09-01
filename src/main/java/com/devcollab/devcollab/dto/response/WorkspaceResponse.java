package com.devcollab.devcollab.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {
    private String id;
    private String name;
    private String description;
    private String ownerName;
    private String ownerId;
    private List<WorkspaceMemberResponse> members;
    private LocalDateTime createdAt;
}