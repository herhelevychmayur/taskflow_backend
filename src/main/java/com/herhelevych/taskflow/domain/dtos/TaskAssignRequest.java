package com.herhelevych.taskflow.domain.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskAssignRequest(
        @NotNull
        UUID assigneeId
) {
}
