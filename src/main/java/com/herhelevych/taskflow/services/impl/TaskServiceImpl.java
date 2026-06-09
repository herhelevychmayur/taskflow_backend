package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.aspect.PreventIfArchived;
import com.herhelevych.taskflow.aspect.TargetType;
import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.TaskPriority;
import com.herhelevych.taskflow.domain.TaskStatus;
import com.herhelevych.taskflow.domain.dtos.TaskCreateRequest;
import com.herhelevych.taskflow.domain.dtos.TaskResponse;
import com.herhelevych.taskflow.domain.dtos.TaskUpdateRequest;
import com.herhelevych.taskflow.domain.entities.ProjectMemberId;
import com.herhelevych.taskflow.domain.entities.Task;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.TaskMapper;
import com.herhelevych.taskflow.repositories.ProjectMemberRepository;
import com.herhelevych.taskflow.repositories.ProjectRepository;
import com.herhelevych.taskflow.repositories.TaskRepository;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public TaskResponse createTask(UUID projectId, TaskCreateRequest request, UUID creatorId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        var creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Creator not found"));
        var assignee = request.assigneeId() == null ? null : getProjectMemberUser(projectId, request.assigneeId());

        var task = Task.builder()
                .project(project)
                .title(request.title())
                .description(request.description())
                .status(TaskStatus.TODO)
                .priority(request.priority())
                .assignee(assignee)
                .creator(creator)
                .dueDate(request.dueDate())
                .build();

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(UUID projectId, TaskStatus status, TaskPriority priority, UUID assigneeId) {

        return taskRepository.findProjectTasks(
                        projectId,
                        status != null ? status.name() : null,
                        priority != null ? priority.name() : null,
                        assigneeId
                ).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID projectId, UUID taskId) {
        return taskMapper.toResponse(getTaskInProject(projectId, taskId));
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public TaskResponse updateTask(UUID projectId, UUID taskId, TaskUpdateRequest request) {
        var task = getTaskInProject(projectId, taskId);
        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.assigneeId() != null) {
            task.setAssignee(getProjectMemberUser(projectId, request.assigneeId()));
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public TaskResponse assignTask(UUID projectId, UUID taskId, UUID assigneeId) {
        var task = getTaskInProject(projectId, taskId);
        task.setAssignee(getProjectMemberUser(projectId, assigneeId));
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public TaskResponse unassignTask(UUID projectId, UUID taskId) {
        var task = getTaskInProject(projectId, taskId);
        task.setAssignee(null);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public TaskResponse updateAssignedTaskStatus(UUID projectId, UUID taskId, UUID userId, TaskStatus status) {
        var task = getTaskInProject(projectId, taskId);
        if (!isTaskAssignee(task, userId)) {
            throw new AccessDeniedException("Only the assigned user can update task status");
        }
        task.setStatus(status);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public void deleteTask(UUID projectId, UUID taskId) {
        var task = getTaskInProject(projectId, taskId);
        taskRepository.delete(task);
    }

    private Task getTaskInProject(UUID projectId, UUID taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        if (!task.getProject().getId().equals(projectId)) {
            throw new EntityNotFoundException("Task not found in project");
        }
        return task;
    }

    private User getProjectMemberUser(UUID projectId, UUID userId) {
        var memberId = new ProjectMemberId(userId, projectId);
        if (!projectMemberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Assignee must be a project member");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private boolean isTaskAssignee(Task task, UUID userId) {
        return task.getAssignee() != null && task.getAssignee().getId().equals(userId);
    }

    private boolean isProjectAdmin(UUID projectId, UUID userId) {
        var memberId = new ProjectMemberId(userId, projectId);
        return projectMemberRepository.findById(memberId)
                .map(member -> member.getRole() == ProjectRole.ROLE_ADMIN)
                .orElse(false);
    }
}
