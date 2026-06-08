package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.dtos.UserResponse;
import com.herhelevych.taskflow.mappers.UserMapper;
import com.herhelevych.taskflow.repositories.CommentRepository;
import com.herhelevych.taskflow.repositories.ProjectInviteRepository;
import com.herhelevych.taskflow.repositories.ProjectMemberRepository;
import com.herhelevych.taskflow.repositories.TaskRepository;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectInviteRepository projectInviteRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse findById(UUID id) {
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        commentRepository.deleteAllByUserId(id);
        taskRepository.unassignAllTasksFromUser(id);
        taskRepository.clearCreatorFromUserTasks(id);
        projectInviteRepository.deleteAllByInviteeIdOrInviterId(id, id);
        projectMemberRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }
}
