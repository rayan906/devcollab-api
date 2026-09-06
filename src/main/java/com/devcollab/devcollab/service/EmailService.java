package com.devcollab.devcollab.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("taskExecutor")
    public void sendTaskAssignedEmail(String toEmail, String userName,
                                      String taskTitle, String projectName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Task Assigned: " + taskTitle);
            message.setText(
                    "Hi " + userName + ",\n\n" +
                            "You have been assigned a new task:\n\n" +
                            "Task: " + taskTitle + "\n" +
                            "Project: " + projectName + "\n\n" +
                            "Log in to DevCollab to view the task details.\n\n" +
                            "Best regards,\nDevCollab"
            );
            mailSender.send(message);
            log.info("Task assigned email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send task assigned email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async("taskExecutor")
    public void sendCommentNotificationEmail(String toEmail, String userName,
                                             String taskTitle, String commenterName,
                                             String comment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("New Comment on: " + taskTitle);
            message.setText(
                    "Hi " + userName + ",\n\n" +
                            commenterName + " commented on your task:\n\n" +
                            "Task: " + taskTitle + "\n" +
                            "Comment: " + comment + "\n\n" +
                            "Log in to DevCollab to reply.\n\n" +
                            "Best regards,\nDevCollab"
            );
            mailSender.send(message);
            log.info("Comment notification email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send comment email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async("taskExecutor")
    public void sendWorkspaceInviteEmail(String toEmail, String userName,
                                         String workspaceName, String inviterName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("You've been invited to: " + workspaceName);
            message.setText(
                    "Hi " + userName + ",\n\n" +
                            inviterName + " has invited you to join the workspace:\n\n" +
                            "Workspace: " + workspaceName + "\n\n" +
                            "Log in to DevCollab to get started.\n\n" +
                            "Best regards,\nDevCollab"
            );
            mailSender.send(message);
            log.info("Workspace invite email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send invite email to {}: {}", toEmail, e.getMessage());
        }
    }
}