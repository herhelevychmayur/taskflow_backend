package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByTaskId(UUID taskId);

    @Modifying
    void deleteAllByUserId(UUID userId);
}
