package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.TaskPriority;
import com.herhelevych.taskflow.domain.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        UUID assigneeId,
        String assigneeFullName,
        String assigneeUsername,
        UUID creatorId,
        String creatorFullName,
        String creatorUsername,
        Instant dueDate,
        Instant createdAt,
        Instant updatedAt
) {
}
