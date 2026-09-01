package com.devcollab.devcollab.repository;

import com.devcollab.devcollab.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findAllByWorkspaceId(String workspaceId);
}