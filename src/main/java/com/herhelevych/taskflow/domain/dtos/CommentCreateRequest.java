package com.herhelevych.taskflow.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank
        @Size(max = 5000)
        String content
) {
}
