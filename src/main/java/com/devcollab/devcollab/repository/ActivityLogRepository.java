package com.devcollab.devcollab.repository;

import com.devcollab.devcollab.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {
    List<ActivityLog> findAllByTaskIdOrderByCreatedAtDesc(String taskId);
}