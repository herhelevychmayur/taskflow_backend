package com.herhelevych.taskflow.domain.dtos;

import java.util.UUID;

public record ProjectShortResponse(
        UUID id,
        String name,
        boolean isArchived
) {
}
