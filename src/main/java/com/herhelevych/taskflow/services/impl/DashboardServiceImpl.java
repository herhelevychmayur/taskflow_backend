package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.GlobalRole;
import com.herhelevych.taskflow.domain.dtos.DashboardStatsResponse;
import com.herhelevych.taskflow.domain.dtos.ProjectResponse;
import com.herhelevych.taskflow.domain.dtos.UserResponse;
import com.herhelevych.taskflow.mappers.ProjectMapper;
import com.herhelevych.taskflow.mappers.UserMapper;
import com.herhelevych.taskflow.repositories.CommentRepository;
import com.herhelevych.taskflow.repositories.ProjectRepository;
import com.herhelevych.taskflow.repositories.TaskRepository;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.services.DashboardService;
import com.herhelevych.taskflow.services.ProjectService;
import com.herhelevych.taskflow.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        return new DashboardStatsResponse(
                projectRepository.count(),
                projectRepository.countByIsArchivedTrue(),
                userRepository.count(),
                userRepository.countByRole(GlobalRole.ROLE_SUPERADMIN),
                taskRepository.count(),
                commentRepository.count()
        );
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId) {
        projectService.deleteProject(projectId);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        userService.deleteUser(userId);
    }

    @Override
    @Transactional
    public UserResponse assignSuperadmin(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setRole(GlobalRole.ROLE_SUPERADMIN);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse demoteSuperadmin(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setRole(GlobalRole.ROLE_USER);
        return userMapper.toResponse(userRepository.save(user));
    }
}
