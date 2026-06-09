package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByTaskId(UUID taskId);

    long countByTaskProjectId(UUID projectId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.task.project.id = :projectId")
    void deleteAllByTaskProjectId(@Param("projectId") UUID projectId);
}
