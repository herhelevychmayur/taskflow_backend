package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.aspect.PreventIfArchived;
import com.herhelevych.taskflow.aspect.TargetType;
import com.herhelevych.taskflow.domain.InviteStatus;
import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.dtos.ProjectCreateRequest;
import com.herhelevych.taskflow.domain.dtos.*;
import com.herhelevych.taskflow.domain.entities.*;
import com.herhelevych.taskflow.mappers.ProjectMapper;
import com.herhelevych.taskflow.repositories.CommentRepository;
import com.herhelevych.taskflow.repositories.ProjectInviteRepository;
import com.herhelevych.taskflow.repositories.ProjectMemberRepository;
import com.herhelevych.taskflow.repositories.ProjectRepository;
import com.herhelevych.taskflow.repositories.TaskRepository;
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
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
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
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public ProjectResponse updateProject(UUID projectId, ProjectCreateRequest request) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        project.setName(request.name());
        project.setDescription(request.description());
        var savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getUsersProjects(UUID userId) {
        return projectMemberRepository.findAllByUserId(userId).stream()
                .map(ProjectMember::getProject)
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public ProjectMemberInviteResponse inviteMember(UUID projectId, UUID inviterId, UUID inviteeId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        var inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new EntityNotFoundException("Inviter not found"));
        var invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new EntityNotFoundException("Invitee not found"));

        if (projectMemberRepository.existsById(new ProjectMemberId(inviteeId, projectId))) {
            throw new IllegalArgumentException("User is already a member of this project");
        }

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
    @PreventIfArchived(type = TargetType.INVITE, idParam = "inviteId")
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
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public ProjectMemberResponse updateMemberRole(UUID projectId, UUID adminId, UUID userId, ProjectRole role) {
        if (userId.equals(adminId))
            throw new IllegalArgumentException("You cannot change your own role");

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
    @PreventIfArchived(type = TargetType.PROJECT, idParam = "projectId")
    public void removeMember(UUID projectId, UUID userId) {
        var memberId = new ProjectMemberId(userId, projectId);
        if (!projectMemberRepository.existsById(memberId)) {
            throw new EntityNotFoundException("Project member not found");
        }
        taskRepository.unassignProjectTasksFromUser(projectId, userId);
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
        commentRepository.deleteAllByTaskProjectId(projectId);
        taskRepository.deleteAllByProjectId(projectId);
        projectInviteRepository.deleteAllByProjectId(projectId);
        projectMemberRepository.deleteAllByProjectId(projectId);
        projectRepository.deleteById(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectMemberResponse getProjectMember(UUID projectId, UUID id) {
        var memberId = new ProjectMemberId(id, projectId);
        return projectMemberRepository.findById(memberId)
                .map(projectMapper::toMemberResponse)
                .orElseThrow(() -> new EntityNotFoundException("Project member not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectStatsResponse getProjectStats(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found");
        }

        long totalTasks = taskRepository.countByProjectId(projectId);
        long totalMembers = projectMemberRepository.findAllByProjectId(projectId).size();
        long totalComments = commentRepository.countByTaskProjectId(projectId);

        long todoTasks = taskRepository.countByProjectIdAndStatus(projectId, com.herhelevych.taskflow.domain.TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByProjectIdAndStatus(projectId, com.herhelevych.taskflow.domain.TaskStatus.IN_PROGRESS);
        long doneTasks = taskRepository.countByProjectIdAndStatus(projectId, com.herhelevych.taskflow.domain.TaskStatus.DONE);

        long lowPriority = taskRepository.countByProjectIdAndPriority(projectId, com.herhelevych.taskflow.domain.TaskPriority.LOW);
        long mediumPriority = taskRepository.countByProjectIdAndPriority(projectId, com.herhelevych.taskflow.domain.TaskPriority.MEDIUM);
        long highPriority = taskRepository.countByProjectIdAndPriority(projectId, com.herhelevych.taskflow.domain.TaskPriority.HIGH);

        long overdueTasks = taskRepository.countByProjectIdAndDueDateBeforeAndStatusNot(
                projectId, java.time.Instant.now(), com.herhelevych.taskflow.domain.TaskStatus.DONE
        );

        return new ProjectStatsResponse(
                totalTasks,
                totalMembers,
                totalComments,
                overdueTasks,
                todoTasks,
                inProgressTasks,
                doneTasks,
                lowPriority,
                mediumPriority,
                highPriority
        );
    }
}
