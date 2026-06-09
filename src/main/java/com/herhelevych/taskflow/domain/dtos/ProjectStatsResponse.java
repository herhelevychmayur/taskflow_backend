package com.herhelevych.taskflow.domain.dtos;

public record ProjectStatsResponse(
        long totalTasks,
        long totalMembers,
        long totalComments,
        long overdueTasks,
        long todoTasks,
        long inProgressTasks,
        long doneTasks,
        long lowPriorityTasks,
        long mediumPriorityTasks,
        long highPriorityTasks
) {
}
