package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.ProjectMember;
import com.herhelevych.taskflow.domain.entities.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findAllByProjectId(UUID projectId);

    List<ProjectMember> findAllByUserId(UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectMember pm WHERE pm.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectMember pm WHERE pm.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") UUID projectId);
}
