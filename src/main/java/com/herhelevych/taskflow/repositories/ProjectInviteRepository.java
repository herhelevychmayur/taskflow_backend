package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.ProjectInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, UUID> {
    List<ProjectInvite> findAllByInviteeId(UUID userId);

    List<ProjectInvite> findAllByProjectId(UUID projectId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectInvite pi WHERE pi.invitee.id = :inviteeId OR pi.inviter.id = :inviterId")
    void deleteAllByInviteeIdOrInviterId(@Param("inviteeId") UUID inviteeId, @Param("inviterId") UUID inviterId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectInvite pi WHERE pi.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") UUID projectId);
}
