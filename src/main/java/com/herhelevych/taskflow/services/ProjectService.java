package com.herhelevych.taskflow.services;

import com.herhelevych.taskflow.domain.InviteStatus;
import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.dtos.*;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectResponse createProject(ProjectCreateRequest request, UUID userId);
    ProjectResponse createProject(UUID projectId);
    ProjectResponse updateProject(UUID projectId, ProjectCreateRequest request);
    List<ProjectShortResponse> getUsersProjects(UUID userId);
    List<ProjectShortResponse> getAllProjects();
    void archiveProject(UUID projectId, boolean isArchived);
    void deleteProject(UUID projectId);
    ProjectMemberInviteResponse inviteMember(UUID projectId, UUID inviterId, UUID inviteeId);
    ProjectMemberInviteResponse respondToInvite(UUID inviteId, UUID inviteeId, InviteStatus status);
    ProjectMemberResponse updateMemberRole(UUID projectId, UUID adminId, UUID userId, ProjectRole role);
    List<ProjectMemberInviteResponse> getInvitesByUser(UUID userId);
    List<ProjectMemberInviteResponse> getInvitesByProject(UUID projectId);
    List<ProjectMemberResponse> getProjectMembers(UUID projectId);
    void removeMember(UUID projectId, UUID userId);
    ProjectMemberResponse getProjectMember(UUID projectId, UUID id);
}
