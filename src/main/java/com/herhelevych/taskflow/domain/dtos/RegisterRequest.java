package com.herhelevych.taskflow.domain.dtos;

public record RegisterRequest(
        String username,
        String password,
        String fullName
) {
}
