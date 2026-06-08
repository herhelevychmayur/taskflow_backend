package com.herhelevych.taskflow.controllers;

import com.herhelevych.taskflow.domain.dtos.CommentCreateRequest;
import com.herhelevych.taskflow.domain.dtos.CommentResponse;
import com.herhelevych.taskflow.domain.dtos.CommentUpdateRequest;
import com.herhelevych.taskflow.security.UserDetailsImpl;
import com.herhelevych.taskflow.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("@sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(projectId, taskId, userDetails.getId(), request));
    }

    @GetMapping
    @PreAuthorize("@sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<List<CommentResponse>> getTaskComments(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ) {
        return ResponseEntity.ok(commentService.getTaskComments(projectId, taskId));
    }

    @PatchMapping("/{commentId}")
    @PreAuthorize("@sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(commentService.updateComment(projectId, taskId, commentId, userDetails.getId(), request));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("@sec.isSuperadmin() || @sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        commentService.deleteComment(projectId, taskId, commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
