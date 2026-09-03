package com.devcollab.devcollab.controller;

import com.devcollab.devcollab.dto.request.CommentRequest;
import com.devcollab.devcollab.dto.request.TaskRequest;
import com.devcollab.devcollab.dto.response.*;
import com.devcollab.devcollab.entity.TaskPriority;
import com.devcollab.devcollab.entity.TaskStatus;
import com.devcollab.devcollab.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // ── Task endpoints ──────────────────────────────────────────

    @PostMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable String projectId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created",
                        taskService.createTask(projectId, request)));
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(
            @PathVariable String projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String assigneeId) {
        return ResponseEntity.ok(ApiResponse.success("Tasks fetched",
                taskService.getTasks(projectId, status, priority, assigneeId)));
    }

    @GetMapping("/api/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @PathVariable String projectId,
            @PathVariable String taskId) {
        return ResponseEntity.ok(ApiResponse.success("Task fetched",
                taskService.getTask(projectId, taskId)));
    }

    @PutMapping("/api/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable String projectId,
            @PathVariable String taskId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Task updated",
                taskService.updateTask(projectId, taskId, request)));
    }

    @DeleteMapping("/api/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable String projectId,
            @PathVariable String taskId) {
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted", null));
    }

    // ── Comment endpoints ───────────────────────────────────────

    @PostMapping("/api/tasks/{taskId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable String taskId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment added",
                        taskService.addComment(taskId, request)));
    }

    @GetMapping("/api/tasks/{taskId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable String taskId) {
        return ResponseEntity.ok(ApiResponse.success("Comments fetched",
                taskService.getComments(taskId)));
    }

    @DeleteMapping("/api/tasks/{taskId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable String taskId,
            @PathVariable String commentId) {
        taskService.deleteComment(taskId, commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted", null));
    }

    // ── Activity log endpoint ───────────────────────────────────

    @GetMapping("/api/tasks/{taskId}/activity")
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getActivityLog(
            @PathVariable String taskId) {
        return ResponseEntity.ok(ApiResponse.success("Activity fetched",
                taskService.getActivityLog(taskId)));
    }
}