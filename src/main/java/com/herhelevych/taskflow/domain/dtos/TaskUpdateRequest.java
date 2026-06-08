package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.TaskPriority;
import com.herhelevych.taskflow.domain.TaskStatus;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record TaskUpdateRequest(
        @Size(max = 150)
        String title,

        @Size(max = 5000)
        String description,

        TaskStatus status,
        TaskPriority priority,
        UUID assigneeId,
        Instant dueDate
) {
}
