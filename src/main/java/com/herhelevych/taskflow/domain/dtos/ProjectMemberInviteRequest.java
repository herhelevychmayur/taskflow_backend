package com.herhelevych.taskflow.domain.dtos;

import java.util.UUID;

public record ProjectMemberInviteRequest(
        UUID projectId,
        UUID inviteeId
) {

}
