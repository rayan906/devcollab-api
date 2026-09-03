package com.devcollab.devcollab.dto.request;

import com.devcollab.devcollab.entity.TaskPriority;
import com.devcollab.devcollab.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private String assigneeId;
    private String parentTaskId;
    private LocalDateTime dueDate;
}