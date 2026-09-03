package com.devcollab.devcollab.service;

import com.devcollab.devcollab.dto.request.CommentRequest;
import com.devcollab.devcollab.dto.request.TaskRequest;
import com.devcollab.devcollab.dto.response.ActivityLogResponse;
import com.devcollab.devcollab.dto.response.CommentResponse;
import com.devcollab.devcollab.dto.response.TaskResponse;
import com.devcollab.devcollab.entity.TaskPriority;
import com.devcollab.devcollab.entity.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(String projectId, TaskRequest request);
    List<TaskResponse> getTasks(String projectId, TaskStatus status,
                                TaskPriority priority, String assigneeId);
    TaskResponse getTask(String projectId, String taskId);
    TaskResponse updateTask(String projectId, String taskId, TaskRequest request);
    void deleteTask(String projectId, String taskId);
    CommentResponse addComment(String taskId, CommentRequest request);
    List<CommentResponse> getComments(String taskId);
    void deleteComment(String taskId, String commentId);
    List<ActivityLogResponse> getActivityLog(String taskId);
}