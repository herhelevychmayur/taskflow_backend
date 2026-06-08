package com.herhelevych.taskflow.domain.dtos;

public record DashboardStatsResponse(
        long projectsCount,
        long archivedProjectsCount,
        long usersCount,
        long superadminsCount,
        long tasksCount,
        long commentsCount
) {
}
