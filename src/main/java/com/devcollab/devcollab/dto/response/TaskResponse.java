package com.devcollab.devcollab.dto.response;

import com.devcollab.devcollab.entity.TaskPriority;
import com.devcollab.devcollab.entity.TaskStatus;
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
public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private String projectId;
    private String reporterId;
    private String reporterName;
    private String assigneeId;
    private String assigneeName;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private String parentTaskId;
    private List<TaskResponse> subtasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}