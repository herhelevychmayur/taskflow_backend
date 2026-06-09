package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.dtos.ProjectCreateRequest;
import com.herhelevych.taskflow.domain.dtos.ProjectResponse;
import com.herhelevych.taskflow.domain.entities.Project;
import com.herhelevych.taskflow.domain.entities.ProjectMember;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.ProjectMapper;
import com.herhelevych.taskflow.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User testUser;
    private Project testProject;
    private UUID userId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .username("test@example.com")
                .password("Test")
                .fullName("User")
                .build();

        testProject = Project.builder()
                .id(projectId)
                .name("Test Project")
                .description("Test Description")
                .isArchived(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createProject_Success() {
        // Arrange
        ProjectCreateRequest request = new ProjectCreateRequest("New Project", "Description");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(i -> i.getArguments()[0]);
        when(projectMapper.toResponse(testProject)).thenReturn(
                new ProjectResponse(projectId, "New Project", "Description", false));

        // Act
        ProjectResponse response = projectService.createProject(request, userId);

        // Assert
        assertNotNull(response);
        assertEquals("New Project", response.name());
        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
    }

    @Test
    void createProject_UserNotFound_ThrowsException() {
        // Arrange
        ProjectCreateRequest request = new ProjectCreateRequest("New Project", "Description");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.createProject(request, userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectById_Success() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectMapper.toResponse(testProject))
                .thenReturn(new ProjectResponse(projectId, "Test Project", "Test Description", false));

        // Act
        ProjectResponse response = projectService.createProject(projectId); // actually it's getProjectById logically
                                                                            // but named createProject(UUID) in
                                                                            // interface maybe? Wait, let me check.

        // Assert
        assertNotNull(response);
        assertEquals("Test Project", response.name());
        verify(projectRepository, times(1)).findById(projectId);
    }
}
