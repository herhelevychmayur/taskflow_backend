package com.herhelevych.taskflow.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 200)
        String description
) {
}
