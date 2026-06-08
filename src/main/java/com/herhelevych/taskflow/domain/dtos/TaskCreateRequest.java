package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record TaskCreateRequest(
        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        @Size(max = 5000)
        String description,

        @NotNull
        TaskPriority priority,

        UUID assigneeId,

        Instant dueDate
) {
}
