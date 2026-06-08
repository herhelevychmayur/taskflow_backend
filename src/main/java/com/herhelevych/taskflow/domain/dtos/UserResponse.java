package com.herhelevych.taskflow.domain.dtos;

import com.herhelevych.taskflow.domain.GlobalRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String fullName,
        GlobalRole role
) {
}
