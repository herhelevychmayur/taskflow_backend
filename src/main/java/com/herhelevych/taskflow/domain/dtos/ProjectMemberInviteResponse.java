package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.InviteStatus;
import java.time.Instant;
import java.util.UUID;

public record ProjectMemberInviteResponse(
        UUID id,
        UUID projectId,
        String projectName,
        UUID inviterId,
        String inviterUsername,
        UUID inviteeId,
        String inviteeUsername,
        InviteStatus status,
        Instant createdAt
) {
}
