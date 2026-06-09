package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.aspect.PreventIfArchived;
import com.herhelevych.taskflow.aspect.TargetType;
import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.dtos.CommentCreateRequest;
import com.herhelevych.taskflow.domain.dtos.CommentResponse;
import com.herhelevych.taskflow.domain.dtos.CommentUpdateRequest;
import com.herhelevych.taskflow.domain.entities.Comment;
import com.herhelevych.taskflow.domain.entities.ProjectMemberId;
import com.herhelevych.taskflow.domain.entities.Task;
import com.herhelevych.taskflow.mappers.CommentMapper;
import com.herhelevych.taskflow.repositories.CommentRepository;
import com.herhelevych.taskflow.repositories.ProjectMemberRepository;
import com.herhelevych.taskflow.repositories.TaskRepository;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.services.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public CommentResponse createComment(UUID projectId, UUID taskId, UUID authorId, CommentCreateRequest request) {
        var task = getTaskInProject(projectId, taskId);
        var author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var comment = Comment.builder()
                .task(task)
                .user(author)
                .content(request.content())
                .build();

        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getTaskComments(UUID projectId, UUID taskId) {
        getTaskInProject(projectId, taskId);
        return commentRepository.findAllByTaskId(taskId).stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public CommentResponse updateComment(UUID projectId, UUID taskId, UUID commentId, UUID userId, CommentUpdateRequest request) {
        var comment = getCommentInTask(projectId, taskId, commentId);
        requireCommentOwnerOrProjectAdmin(projectId, comment, userId);
        comment.setContent(request.content());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public void deleteComment(UUID projectId, UUID taskId, UUID commentId, UUID userId) {
        var comment = getCommentInTask(projectId, taskId, commentId);
        requireCommentOwnerOrProjectAdmin(projectId, comment, userId);
        commentRepository.delete(comment);
    }

    private Comment getCommentInTask(UUID projectId, UUID taskId, UUID commentId) {
        var task = getTaskInProject(projectId, taskId);
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (!comment.getTask().getId().equals(task.getId())) {
            throw new EntityNotFoundException("Comment not found in task");
        }
        return comment;
    }

    private Task getTaskInProject(UUID projectId, UUID taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        if (!task.getProject().getId().equals(projectId)) {
            throw new EntityNotFoundException("Task not found in project");
        }
        return task;
    }

    private void requireCommentOwnerOrProjectAdmin(UUID projectId, Comment comment, UUID userId) {
        if (comment.getUser().getId().equals(userId) || isProjectAdmin(projectId, userId)) {
            return;
        }
        throw new AccessDeniedException("Only comment author or project admin can modify this comment");
    }

    private boolean isProjectAdmin(UUID projectId, UUID userId) {
        var memberId = new ProjectMemberId(userId, projectId);
        return projectMemberRepository.findById(memberId)
                .map(member -> member.getRole() == ProjectRole.ROLE_ADMIN)
                .orElse(false);
    }
}
