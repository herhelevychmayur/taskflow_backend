package com.herhelevych.taskflow.controllers;

import com.herhelevych.taskflow.domain.dtos.DashboardStatsResponse;
import com.herhelevych.taskflow.domain.dtos.ProjectResponse;
import com.herhelevych.taskflow.domain.dtos.UserResponse;
import com.herhelevych.taskflow.security.UserDetailsImpl;
import com.herhelevych.taskflow.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        return ResponseEntity.ok(dashboardService.getProjects());
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        dashboardService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(dashboardService.getUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        dashboardService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/superadmin")
    public ResponseEntity<UserResponse> assignSuperadmin(@PathVariable UUID userId) {
        return ResponseEntity.ok(dashboardService.assignSuperadmin(userId));
    }

    @PatchMapping("/users/{userId}/demote")
    public ResponseEntity<UserResponse> demoteSuperadmin(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails.getId().equals(userId)) {
            throw new IllegalArgumentException("You cannot demote yourself");
        }
        return ResponseEntity.ok(dashboardService.demoteSuperadmin(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
