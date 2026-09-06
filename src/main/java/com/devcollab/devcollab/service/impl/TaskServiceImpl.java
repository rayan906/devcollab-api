package com.devcollab.devcollab.service.impl;

import com.devcollab.devcollab.dto.request.CommentRequest;
import com.devcollab.devcollab.dto.request.TaskRequest;
import com.devcollab.devcollab.dto.response.ActivityLogResponse;
import com.devcollab.devcollab.dto.response.CommentResponse;
import com.devcollab.devcollab.dto.response.TaskResponse;
import com.devcollab.devcollab.entity.*;
import com.devcollab.devcollab.repository.*;
import com.devcollab.devcollab.service.EmailService;
import com.devcollab.devcollab.service.TaskService;
import com.devcollab.devcollab.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;

    @Override
    @Transactional
    public TaskResponse createTask(String projectId, TaskRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Project project = getProjectOrThrow(projectId);
        verifyWorkspaceMember(project.getWorkspace().getId());

        Task.TaskBuilder builder = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .project(project)
                .reporter(currentUser)
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            builder.assignee(assignee);
        }

        if (request.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(request.getParentTaskId())
                    .orElseThrow(() -> new RuntimeException("Parent task not found"));
            builder.parentTask(parentTask);
        }

        Task saved = taskRepository.save(builder.build());

        logActivity(saved, currentUser, "TASK_CREATED", null, saved.getTitle());

        if (request.getAssigneeId() != null && saved.getAssignee() != null) {
            emailService.sendTaskAssignedEmail(
                    saved.getAssignee().getEmail(),
                    saved.getAssignee().getName(),
                    saved.getTitle(),
                    project.getName()
            );
        }

        return mapToResponse(saved);
    }

    @Override
    public List<TaskResponse> getTasks(String projectId, TaskStatus status,
                                       TaskPriority priority, String assigneeId) {
        Project project = getProjectOrThrow(projectId);
        verifyWorkspaceMember(project.getWorkspace().getId());

        List<Task> tasks;

        if (status != null) {
            tasks = taskRepository.findAllByProjectIdAndStatus(projectId, status);
        } else if (priority != null) {
            tasks = taskRepository.findAllByProjectIdAndPriority(projectId, priority);
        } else if (assigneeId != null) {
            tasks = taskRepository.findAllByProjectIdAndAssigneeId(projectId, assigneeId);
        } else {
            tasks = taskRepository.findAllByProjectId(projectId);
        }

        // Only return top-level tasks (no subtasks in the list)
        return tasks.stream()
                .filter(t -> t.getParentTask() == null)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TaskResponse getTask(String projectId, String taskId) {
        Project project = getProjectOrThrow(projectId);
        verifyWorkspaceMember(project.getWorkspace().getId());
        Task task = getTaskOrThrow(taskId);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String projectId, String taskId, TaskRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Project project = getProjectOrThrow(projectId);
        verifyWorkspaceMember(project.getWorkspace().getId());
        Task task = getTaskOrThrow(taskId);

        // Log status change
        if (request.getStatus() != null && request.getStatus() != task.getStatus()) {
            logActivity(task, currentUser, "STATUS_CHANGED",
                    task.getStatus().name(), request.getStatus().name());
            task.setStatus(request.getStatus());
        }

        // Log priority change
        if (request.getPriority() != null && request.getPriority() != task.getPriority()) {
            logActivity(task, currentUser, "PRIORITY_CHANGED",
                    task.getPriority().name(), request.getPriority().name());
            task.setPriority(request.getPriority());
        }

        // Log assignee change
        if (request.getAssigneeId() != null) {
            User newAssignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            String oldAssigneeName = task.getAssignee() != null ? task.getAssignee().getName() : "Unassigned";
            logActivity(task, currentUser, "ASSIGNEE_CHANGED",
                    oldAssigneeName, newAssignee.getName());
            task.setAssignee(newAssignee);
            emailService.sendTaskAssignedEmail(
                    newAssignee.getEmail(),
                    newAssignee.getName(),
                    task.getTitle(),
                    task.getProject().getName()
            );
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(String projectId, String taskId) {
        Project project = getProjectOrThrow(projectId);
        verifyWorkspaceMember(project.getWorkspace().getId());
        Task task = getTaskOrThrow(taskId);
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public CommentResponse addComment(String taskId, CommentRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Task task = getTaskOrThrow(taskId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .user(currentUser)
                .build();

        Comment saved = commentRepository.save(comment);
        if (!task.getReporter().getId().equals(currentUser.getId())) {
            emailService.sendCommentNotificationEmail(
                    task.getReporter().getEmail(),
                    task.getReporter().getName(),
                    task.getTitle(),
                    currentUser.getName(),
                    request.getContent()
            );
        }
        logActivity(task, currentUser, "COMMENT_ADDED", null, request.getContent());

        return mapCommentToResponse(saved);
    }

    @Override
    public List<CommentResponse> getComments(String taskId) {
        getTaskOrThrow(taskId);
        return commentRepository.findAllByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::mapCommentToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteComment(String taskId, String commentId) {
        User currentUser = securityUtil.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    @Override
    public List<ActivityLogResponse> getActivityLog(String taskId) {
        getTaskOrThrow(taskId);
        return activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(this::mapActivityToResponse)
                .toList();
    }

    // ── Helper methods ──────────────────────────────────────────

    private Project getProjectOrThrow(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private Task getTaskOrThrow(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    private void verifyWorkspaceMember(String workspaceId) {
        String currentUserId = securityUtil.getCurrentUserId();
        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, currentUserId)) {
            throw new RuntimeException("You are not a member of this workspace");
        }
    }

    private void logActivity(Task task, User user, String action,
                             String oldValue, String newValue) {
        ActivityLog log = ActivityLog.builder()
                .task(task)
                .user(user)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        activityLogRepository.save(log);
    }

    private TaskResponse mapToResponse(Task task) {
        List<TaskResponse> subtasks = taskRepository
                .findAllByParentTaskId(task.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .projectId(task.getProject().getId())
                .reporterId(task.getReporter().getId())
                .reporterName(task.getReporter().getName())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeName(task.getAssignee() != null ? task.getAssignee().getName() : null)
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .parentTaskId(task.getParentTask() != null ? task.getParentTask().getId() : null)
                .subtasks(subtasks)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private ActivityLogResponse mapActivityToResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .userId(log.getUser().getId())
                .userName(log.getUser().getName())
                .createdAt(log.getCreatedAt())
                .build();
    }
}