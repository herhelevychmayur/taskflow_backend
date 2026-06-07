package com.herhelevych.taskflow.domain.dtos;

import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        boolean isArchived
){
}

