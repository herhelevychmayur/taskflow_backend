package com.herhelevych.taskflow.controllers;

import com.herhelevych.taskflow.domain.InviteStatus;
import com.herhelevych.taskflow.domain.ProjectRole;
import com.herhelevych.taskflow.domain.dtos.*;
import com.herhelevych.taskflow.security.UserDetailsImpl;
import com.herhelevych.taskflow.services.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        var projectResponse = projectService.createProject(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponse);
    }



    @GetMapping
    public ResponseEntity<List<ProjectShortResponse>> getProjects(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.getUsersProjects(userDetails.getId()));
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("@sec.isSuperadmin() || @sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.createProject(projectId));
    }

    @PatchMapping("/{projectId}")
    @PreAuthorize("@sec.hasProjectRole(#projectId, 'ROLE_ADMIN')")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        return ResponseEntity.ok(projectService.updateProject(projectId, request));
    }

    @PatchMapping("/{projectId}/archive")
    @PreAuthorize("@sec.hasProjectRole(#projectId, 'ROLE_ADMIN')")
    public ResponseEntity<Void> archiveProject(
            @PathVariable UUID projectId,
            @RequestParam boolean isArchived
    ) {
        projectService.archiveProject(projectId, isArchived);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("@sec.hasProjectRole(#projectId, 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{projectId}/members")
    @PreAuthorize("@sec.isSuperadmin() || @sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.getProjectMembers(projectId));
    }

    @GetMapping("/{projectId}/role")
    @PreAuthorize("@sec.hasAnyProjectRole(#projectId, 'ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ProjectMemberResponse> getMemberRole(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.getProjectMember(projectId, userDetails.getId()));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("@sec.hasProjectRole(#projectId, 'ROLE_ADMIN')")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID projectId,
            @PathVariable UUID userId
    ) {
        projectService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{projectId}/members/{userId}/role")
    @PreAuthorize("@sec.hasProjectRole(#projectId, 'ROLE_ADMIN')")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(
            @PathVariable UUID projectId,
            @PathVariable UUID userId,
            @NotNull @RequestBody ProjectRole role,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.updateMemberRole(projectId, userDetails.getId(), userId, role));
    }

    @PostMapping("/{projectId}/invites/{inviteeId}")
    @PreAuthorize("@sec.hasProjectRole(#projectId, 'ROLE_ADMIN')")
    public ResponseEntity<ProjectMemberInviteResponse> inviteMember(
            @PathVariable UUID projectId,
            @PathVariable UUID inviteeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.inviteMember(projectId, userDetails.getId(), inviteeId));
    }

    @PatchMapping("/invites/{inviteId}")
    public ResponseEntity<ProjectMemberInviteResponse> respondToInvite(
            @PathVariable UUID inviteId,
            @NotNull @RequestBody InviteStatus status,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
                projectService.respondToInvite(inviteId, userDetails.getId(), status)
        );
    }
    
    @GetMapping("/invites/user")
    public ResponseEntity<List<ProjectMemberInviteResponse>> getInvitesByUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(projectService.getInvitesByUser(userDetails.getId()));
    }

    @GetMapping("/{projectId}/invites")
    @PreAuthorize("@sec.hasProjectRole(#projectId, 'ROLE_ADMIN')")
    public ResponseEntity<List<ProjectMemberInviteResponse>> getInvitesByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.getInvitesByProject(projectId));
    }
}
