package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
        @NotNull
        TaskStatus status
) {
}
