package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.ProjectRole;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID userId,
        String fullName,
        ProjectRole role
) {
}
