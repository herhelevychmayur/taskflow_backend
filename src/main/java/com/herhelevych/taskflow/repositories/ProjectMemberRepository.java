package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.ProjectMember;
import com.herhelevych.taskflow.domain.entities.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findAllByProjectId(UUID projectId);

    List<ProjectMember> findAllByUserId(UUID userId);

    @Modifying
    void deleteAllByUserId(UUID userId);

    @Modifying
    void deleteAllByProjectId(UUID projectId);
}
