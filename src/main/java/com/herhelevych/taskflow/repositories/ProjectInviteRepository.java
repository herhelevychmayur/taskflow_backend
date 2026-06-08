package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.ProjectInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.UUID;

public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, UUID> {
    List<ProjectInvite> findAllByInviteeId(UUID userId);

    List<ProjectInvite> findAllByProjectId(UUID projectId);

    @Modifying
    void deleteAllByInviteeIdOrInviterId(UUID inviteeId, UUID inviterId);

    @Modifying
    void deleteAllByProjectId(UUID projectId);
}
