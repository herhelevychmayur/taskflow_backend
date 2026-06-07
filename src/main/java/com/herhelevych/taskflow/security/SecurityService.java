package com.herhelevych.taskflow.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("sec") // short alias
@RequiredArgsConstructor
public class SecurityService {

    private Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean hasProjectRole(UUID projectId, String role) {
        String authority = role + "_" + projectId.toString();
        return getAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public boolean isProjectOwnerOrSuperadmin(UUID projectId) {
        return hasProjectRole(projectId, "OWNER") || isSuperadmin();
    }

    public boolean isSuperadmin() {
        return getAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
    }

}
