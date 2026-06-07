package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.ProjectMember;
import com.herhelevych.taskflow.domain.entities.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findAllByProjectId(UUID projectId);

    List<ProjectMember> findAllByUserId(UUID userId);
}
