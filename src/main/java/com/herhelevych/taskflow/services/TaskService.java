package com.herhelevych.taskflow.services;

import com.herhelevych.taskflow.domain.TaskPriority;
import com.herhelevych.taskflow.domain.TaskStatus;
import com.herhelevych.taskflow.domain.dtos.TaskCreateRequest;
import com.herhelevych.taskflow.domain.dtos.TaskResponse;
import com.herhelevych.taskflow.domain.dtos.TaskUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(UUID projectId, TaskCreateRequest request, UUID creatorId);

    List<TaskResponse> getProjectTasks(UUID projectId, TaskStatus status, TaskPriority priority, UUID assigneeId);

    TaskResponse getTask(UUID projectId, UUID taskId);

    TaskResponse updateTask(UUID projectId, UUID taskId, TaskUpdateRequest request);

    TaskResponse assignTask(UUID projectId, UUID taskId, UUID assigneeId);

    TaskResponse unassignTask(UUID projectId, UUID taskId);

    TaskResponse updateAssignedTaskStatus(UUID projectId, UUID taskId, UUID userId, TaskStatus status);

    void deleteTask(UUID projectId, UUID taskId);
}
