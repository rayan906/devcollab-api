package com.devcollab.devcollab.repository;

import com.devcollab.devcollab.entity.Task;
import com.devcollab.devcollab.entity.TaskPriority;
import com.devcollab.devcollab.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findAllByProjectId(String projectId);

    List<Task> findAllByProjectIdAndStatus(String projectId, TaskStatus status);

    List<Task> findAllByProjectIdAndPriority(String projectId, TaskPriority priority);

    List<Task> findAllByProjectIdAndAssigneeId(String projectId, String assigneeId);

    List<Task> findAllByParentTaskId(String parentTaskId);
}