package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.InviteStatus;
import java.time.Instant;
import java.util.UUID;

public record ProjectMemberInviteResponse(
        UUID id,
        UUID projectId,
        String projectName,
        UUID inviterId,
        String inviterFullName,
        UUID inviteeId,
        String inviteeFullName,
        InviteStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
