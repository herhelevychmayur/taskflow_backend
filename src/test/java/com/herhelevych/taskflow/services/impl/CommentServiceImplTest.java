package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.dtos.CommentCreateRequest;
import com.herhelevych.taskflow.domain.dtos.CommentResponse;
import com.herhelevych.taskflow.domain.dtos.CommentUpdateRequest;
import com.herhelevych.taskflow.domain.entities.Comment;
import com.herhelevych.taskflow.domain.entities.Project;
import com.herhelevych.taskflow.domain.entities.ProjectMember;
import com.herhelevych.taskflow.domain.entities.ProjectMemberId;
import com.herhelevych.taskflow.domain.entities.Task;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.CommentMapper;
import com.herhelevych.taskflow.repositories.CommentRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User testUser;
    private Project testProject;
    private Task testTask;
    private Comment testComment;
    private UUID userId;
    private UUID projectId;
    private UUID taskId;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .username("test@example.com")
                .password("password")
                .fullName("Test User")
                .build();

        testProject = Project.builder()
                .id(projectId)
                .name("Test Project")
                .build();

        testTask = Task.builder()
                .id(taskId)
                .project(testProject)
                .title("Test Task")
                .build();

        testComment = Comment.builder()
                .id(commentId)
                .task(testTask)
                .user(testUser)
                .content("Test Comment")
                .build();
    }

    @Test
    void createComment_Success() {
        // Arrange
        CommentCreateRequest request = new CommentCreateRequest("New Comment");
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentResponse expectedResponse = new CommentResponse(
                commentId, taskId, userId, "Test User", "test@example.com", "New Comment", Instant.now(), Instant.now()
        );
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(expectedResponse);

        // Act
        CommentResponse response = commentService.createComment(projectId, taskId, userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("New Comment", response.content());
        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(userId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_Success() {
        // Arrange
        CommentUpdateRequest request = new CommentUpdateRequest("Updated Comment");
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentResponse expectedResponse = new CommentResponse(
                commentId, taskId, userId, "Test User", "test@example.com", "Updated Comment", Instant.now(), Instant.now()
        );
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(expectedResponse);

        // Act
        CommentResponse response = commentService.updateComment(projectId, taskId, commentId, userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Comment", response.content());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_NotAuthorAndNotAdmin_ThrowsException() {
        // Arrange
        CommentUpdateRequest request = new CommentUpdateRequest("Updated Comment");
        
        UUID anotherUserId = UUID.randomUUID();
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        
        // Mock that anotherUserId is not an admin
        ProjectMemberId memberId = new ProjectMemberId(anotherUserId, projectId);
        when(projectMemberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            commentService.updateComment(projectId, taskId, commentId, anotherUserId, request);
        });

        assertEquals("Only comment author or project admin can modify this comment", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_Success_AsAuthor() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));

        // Act
        commentService.deleteComment(projectId, taskId, commentId, userId);

        // Assert
        verify(commentRepository, times(1)).delete(testComment);
    }
}
