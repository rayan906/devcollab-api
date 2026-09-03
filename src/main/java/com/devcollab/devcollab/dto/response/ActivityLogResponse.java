package com.devcollab.devcollab.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
    private String id;
    private String action;
    private String oldValue;
    private String newValue;
    private String userId;
    private String userName;
    private LocalDateTime createdAt;
}