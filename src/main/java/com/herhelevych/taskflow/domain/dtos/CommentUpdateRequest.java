package com.herhelevych.taskflow.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotBlank
        @Size(max = 5000)
        String content
) {
}
