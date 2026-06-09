package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.TaskPriority;
import com.herhelevych.taskflow.domain.TaskStatus;
import com.herhelevych.taskflow.domain.dtos.TaskCreateRequest;
import com.herhelevych.taskflow.domain.dtos.TaskResponse;
import com.herhelevych.taskflow.domain.dtos.TaskUpdateRequest;
import com.herhelevych.taskflow.domain.entities.Project;
import com.herhelevych.taskflow.domain.entities.ProjectMemberId;
import com.herhelevych.taskflow.domain.entities.Task;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.TaskMapper;
import com.herhelevych.taskflow.repositories.ProjectMemberRepository;
import com.herhelevych.taskflow.repositories.ProjectRepository;
import com.herhelevych.taskflow.repositories.TaskRepository;
import com.herhelevych.taskflow.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private Project testProject;
    private Task testTask;
    private UUID userId;
    private UUID projectId;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .username("test@example.com")
                .password("Test")
                .fullName("User")
                .build();

        testProject = Project.builder()
                .id(projectId)
                .name("Test Project")
                .build();

        testTask = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .project(testProject)
                .creator(testUser)
                .build();
    }

    @Test
    void createTask_Success() {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest(
                "New Task",
                "Description",
                TaskPriority.HIGH,
                null,
                Instant.now().plusSeconds(3600)
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse expectedResponse = new TaskResponse(
                taskId, projectId, "New Task", "Description", TaskStatus.TODO, TaskPriority.HIGH,
                null, null, null, userId, "User", "test@example.com", Instant.now().plusSeconds(3600), Instant.now(), Instant.now()
        );
        when(taskMapper.toResponse(any(Task.class))).thenReturn(expectedResponse);

        // Act
        TaskResponse response = taskService.createTask(projectId, request, userId);

        // Assert
        assertNotNull(response);
        assertEquals("New Task", response.title());
        verify(projectRepository, times(1)).findById(projectId);
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_Success() {
        // Arrange
        TaskUpdateRequest request = new TaskUpdateRequest(
                "Updated Title",
                "Updated Description",
                TaskStatus.IN_PROGRESS,
                TaskPriority.LOW,
                null,
                null
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse expectedResponse = new TaskResponse(
                taskId, projectId, "Updated Title", "Updated Description", TaskStatus.IN_PROGRESS, TaskPriority.LOW,
                null, null, null, userId, "User", "test@example.com", null, Instant.now(), Instant.now()
        );
        when(taskMapper.toResponse(any(Task.class))).thenReturn(expectedResponse);

        // Act
        TaskResponse response = taskService.updateTask(projectId, taskId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Title", response.title());
        assertEquals(TaskStatus.IN_PROGRESS, response.status());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateAssignedTaskStatus_Success() {
        // Arrange
        testTask.setAssignee(testUser); // make testUser the assignee
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse expectedResponse = new TaskResponse(
                taskId, projectId, "Test Task", "Test Description", TaskStatus.DONE, TaskPriority.MEDIUM,
                userId, "User", "test@example.com", userId, "User", "test@example.com", null, Instant.now(), Instant.now()
        );
        when(taskMapper.toResponse(any(Task.class))).thenReturn(expectedResponse);

        // Act
        TaskResponse response = taskService.updateAssignedTaskStatus(projectId, taskId, userId, TaskStatus.DONE);

        // Assert
        assertNotNull(response);
        assertEquals(TaskStatus.DONE, response.status());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateAssignedTaskStatus_NotAssignee_ThrowsException() {
        // Arrange
        testTask.setAssignee(null); // not assigned to the user
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // Act & Assert
        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            taskService.updateAssignedTaskStatus(projectId, taskId, userId, TaskStatus.DONE);
        });

        assertEquals("Only the assigned user can update task status", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }
}
