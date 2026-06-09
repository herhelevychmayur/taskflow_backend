package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.GlobalRole;
import com.herhelevych.taskflow.domain.dtos.UserResponse;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.UserMapper;
import com.herhelevych.taskflow.repositories.CommentRepository;
import com.herhelevych.taskflow.repositories.ProjectInviteRepository;
import com.herhelevych.taskflow.repositories.ProjectMemberRepository;
import com.herhelevych.taskflow.repositories.TaskRepository;
import com.herhelevych.taskflow.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectInviteRepository projectInviteRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .username("test")
                .password("password")
                .fullName("Test User")
                .build();
    }

    @Test
    void findById_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        UserResponse expectedResponse = new UserResponse(userId, "test", "Test User", GlobalRole.ROLE_USER);
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        // Act
        UserResponse response = userService.findById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("test", response.username());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void findAll_Success() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        UserResponse expectedResponse = new UserResponse(userId, "test", "Test User", GlobalRole.ROLE_USER);
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        // Act
        List<UserResponse> responses = userService.findAll();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(commentRepository, times(1)).deleteAllByUserId(userId);
        verify(taskRepository, times(1)).unassignAllTasksFromUser(userId);
        verify(taskRepository, times(1)).clearCreatorFromUserTasks(userId);
        verify(projectInviteRepository, times(1)).deleteAllByInviteeIdOrInviterId(userId, userId);
        verify(projectMemberRepository, times(1)).deleteAllByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }
}
