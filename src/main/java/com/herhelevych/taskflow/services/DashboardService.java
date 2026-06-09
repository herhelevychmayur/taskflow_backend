package com.herhelevych.taskflow.services;

import com.herhelevych.taskflow.domain.dtos.DashboardStatsResponse;
import com.herhelevych.taskflow.domain.dtos.ProjectResponse;
import com.herhelevych.taskflow.domain.dtos.UserResponse;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    List<ProjectResponse> getProjects();

    List<UserResponse> getUsers();

    DashboardStatsResponse getStats();

    void deleteProject(UUID projectId);

    void deleteUser(UUID userId);

    UserResponse assignSuperadmin(UUID userId);

    UserResponse demoteSuperadmin(UUID userId);
}
