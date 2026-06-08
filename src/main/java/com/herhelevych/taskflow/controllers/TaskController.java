package com.herhelevych.taskflow.controllers;

import com.herhelevych.taskflow.domain.TaskPriority;
import com.herhelevych.taskflow.domain.TaskStatus;
import com.herhelevych.taskflow.domain.dtos.TaskAssignRequest;
import com.herhelevych.taskflow.domain.dtos.TaskCreateRequest;
import com.herhelevych.taskflow.domain.dtos.TaskResponse;
import com.herhelevych.taskflow.domain.dtos.TaskStatusUpdateRequest;
import com.herhelevych.taskflow.domain.dtos.TaskUpdateRequest;
import com.herhelevych.taskflow.security.UserDetailsImpl;
import com.herhelevych.taskflow.services.TaskService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("@sec.isProjectAdminOrSuperadmin(#projectId)")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, request, userDetails.getId()));
    }

    @GetMapping
    @PreAuthorize("@sec.isSuperadmin() || @sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID assigneeId
    ) {
        return ResponseEntity.ok(taskService.getProjectTasks(projectId, status, priority, assigneeId));
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("@sec.isSuperadmin() || @sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ) {
        return ResponseEntity.ok(taskService.getTask(projectId, taskId));
    }

    @PatchMapping("/{taskId}")
    @PreAuthorize("@sec.isProjectAdminOrSuperadmin(#projectId)")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTask(projectId, taskId, request));
    }

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("@sec.isSuperadmin() || @sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(taskService.updateAssignedTaskStatus(projectId, taskId, userDetails.getId(), request.status()));
    }

    @PatchMapping("/{taskId}/assignee")
    @PreAuthorize("@sec.isProjectAdminOrSuperadmin(#projectId)")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskAssignRequest request
    ) {
        return ResponseEntity.ok(taskService.assignTask(projectId, taskId, request.assigneeId()));
    }

    @DeleteMapping("/{taskId}/assignee")
    @PreAuthorize("@sec.isProjectAdminOrSuperadmin(#projectId)")
    public ResponseEntity<TaskResponse> unassignTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ) {
        return ResponseEntity.ok(taskService.unassignTask(projectId, taskId));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("@sec.isProjectAdminOrSuperadmin(#projectId)")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ) {
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}
