package com.devcollab.devcollab.dto.response;

import com.devcollab.devcollab.entity.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMemberResponse {
    private String userId;
    private String name;
    private String email;
    private WorkspaceRole role;
    private LocalDateTime joinedAt;
}