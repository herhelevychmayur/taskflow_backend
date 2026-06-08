package com.herhelevych.taskflow.services;

import com.herhelevych.taskflow.domain.dtos.CommentCreateRequest;
import com.herhelevych.taskflow.domain.dtos.CommentResponse;
import com.herhelevych.taskflow.domain.dtos.CommentUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse createComment(UUID projectId, UUID taskId, UUID authorId, CommentCreateRequest request);

    List<CommentResponse> getTaskComments(UUID projectId, UUID taskId);

    CommentResponse updateComment(UUID projectId, UUID taskId, UUID commentId, UUID userId, CommentUpdateRequest request);

    void deleteComment(UUID projectId, UUID taskId, UUID commentId, UUID userId);
}
