package com.herhelevych.taskflow.domain.dtos;

public record LoginRequest(
        String username,
        String password
) {
}
