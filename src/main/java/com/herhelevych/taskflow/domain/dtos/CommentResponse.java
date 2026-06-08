package com.herhelevych.taskflow.domain.dtos;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID taskId,
        UUID authorId,
        String authorFullName,
        String authorUsername,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}
