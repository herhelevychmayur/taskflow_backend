package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.InviteStatus;
import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.dtos.ProjectCreateRequest;
import com.herhelevych.taskflow.domain.dtos.*;
import com.herhelevych.taskflow.domain.entities.*;
import com.herhelevych.taskflow.mappers.ProjectMapper;
import com.herhelevych.taskflow.repositories.ProjectInviteRepository;
import com.herhelevych.taskflow.repositories.ProjectMemberRepository;
import com.herhelevych.taskflow.repositories.ProjectRepository;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.services.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectInviteRepository projectInviteRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request, UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")
        );

        var project = Project.builder()
                .name(request.name())
                .description(request.description())
                .isArchived(false)
                .build();

        var savedProject = projectRepository.save(project);

        var projectMemberId = new ProjectMemberId(userId,savedProject.getId());

        var projectMember = ProjectMember.builder() // add user as admin
                .id(projectMemberId)
                .user(user)
                .project(savedProject)
                .role(ProjectRole.ROLE_ADMIN)
                .build();

        projectMemberRepository.save(projectMember);

        return projectMapper.toResponse(savedProject);
    }


    @Override
    @Transactional(readOnly = true)
    public ProjectResponse createProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .map(projectMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectShortResponse> getUsersProjects(UUID userId) {
        return projectMemberRepository.findAllByUserId(userId).stream()
                .map(ProjectMember::getProject)
                .map(projectMapper::toShortResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectShortResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toShortResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProjectMemberInviteResponse inviteMember(UUID projectId, UUID inviterId, UUID inviteeId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        var inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new EntityNotFoundException("Inviter not found"));
        var invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new EntityNotFoundException("Invitee not found"));

        var invite = ProjectInvite.builder()
                .project(project)
                .invitee(invitee)
                .inviter(inviter)
                .status(InviteStatus.PENDING)
                .build();

        var savedInvite = projectInviteRepository.save(invite);
        return projectMapper.toInviteResponse(savedInvite);
    }

    @Override
    @Transactional
    public ProjectMemberInviteResponse respondToInvite(UUID inviteId, UUID inviteeId, InviteStatus status) {
        var invite = projectInviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found"));
        var invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new EntityNotFoundException("Invitee not found"));
        if (!invite.getInvitee().getId().equals(invitee.getId())) {
            throw new AccessDeniedException("You are not authorized to respond to this invite");
        }

        invite.setStatus(status);

        if (status == InviteStatus.ACCEPTED) {
            var projectMemberId = new ProjectMemberId(invite.getInvitee().getId(), invite.getProject().getId());
            var projectMember = ProjectMember.builder()
                    .id(projectMemberId)
                    .user(invite.getInvitee())
                    .project(invite.getProject())
                    .role(ProjectRole.ROLE_MEMBER)
                    .build();
            projectMemberRepository.save(projectMember);
        }

        var savedInvite = projectInviteRepository.save(invite);
        return projectMapper.toInviteResponse(savedInvite);
    }

    @Override
    @Transactional
    public ProjectMemberResponse updateMemberRole(UUID projectId, UUID userId, ProjectRole role) {
        var memberId = new ProjectMemberId(userId, projectId);
        var member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Project member not found"));

        member.setRole(role);
        var updatedMember = projectMemberRepository.save(member);
        return projectMapper.toMemberResponse(updatedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberInviteResponse> getInvitesByUser(UUID userId) {
        return projectInviteRepository.findAllByInviteeId(userId).stream()
                .map(projectMapper::toInviteResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberInviteResponse> getInvitesByProject(UUID projectId) {
        return projectInviteRepository.findAllByProjectId(projectId).stream()
                .map(projectMapper::toInviteResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(UUID projectId) {
        return projectMemberRepository.findAllByProjectId(projectId).stream()
                .map(projectMapper::toMemberResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        var memberId = new ProjectMemberId(userId, projectId);
        if (!projectMemberRepository.existsById(memberId)) {
            throw new EntityNotFoundException("Project member not found");
        }
        projectMemberRepository.deleteById(memberId);
    }

    @Override
    @Transactional
    public void archiveProject(UUID projectId, boolean isArchived) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        project.setArchived(isArchived);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found");
        }
        projectRepository.deleteById(projectId);
    }
}
