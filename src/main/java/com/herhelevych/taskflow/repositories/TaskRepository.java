package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.TaskPriority;
import com.herhelevych.taskflow.domain.TaskStatus;
import com.herhelevych.taskflow.domain.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    long countByProjectId(UUID projectId);

    long countByProjectIdAndStatus(UUID projectId, TaskStatus status);

    long countByProjectIdAndPriority(UUID projectId, TaskPriority priority);

    long countByProjectIdAndDueDateBeforeAndStatusNot(UUID projectId, Instant now, TaskStatus status);

    @Query(value = """
            SELECT t.id,
                           t.title,
                           t.description,
                           t.status,
                           t.priority,
                           t.project_id,
                           t.assignee_id,
                           t.creator_id,
                           t.created_at,
                           t.due_date,
                           t.updated_at
                    FROM tasks t
            WHERE t.project_id = :projectId
               AND (:status IS NULL OR t.status = CAST(:status AS taskstatus))
               AND (:priority IS NULL OR t.priority = CAST(:priority AS taskpriority))
               AND (:assigneeId IS NULL OR t.assignee_id = CAST(:assigneeId AS UUID))
            """, nativeQuery = true)
    List<Task> findProjectTasks(
            @Param("projectId") UUID projectId,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("assigneeId") UUID assigneeId
    );

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Task t SET t.assignee = NULL WHERE t.project.id = :projectId AND t.assignee.id = :userId")
    void unassignProjectTasksFromUser(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Task t SET t.assignee = NULL WHERE t.assignee.id = :userId")
    void unassignAllTasksFromUser(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Task t SET t.creator = NULL WHERE t.creator.id = :userId")
    void clearCreatorFromUserTasks(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Task t WHERE t.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") UUID projectId);
}
