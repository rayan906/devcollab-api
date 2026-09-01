package com.devcollab.devcollab.repository;

import com.devcollab.devcollab.entity.WorkspaceMember;
import com.devcollab.devcollab.entity.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, String> {

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(String workspaceId, String userId);

    boolean existsByWorkspaceIdAndUserId(String workspaceId, String userId);

    List<WorkspaceMember> findAllByWorkspaceId(String workspaceId);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserIdAndRole(
            String workspaceId, String userId, WorkspaceRole role);
}