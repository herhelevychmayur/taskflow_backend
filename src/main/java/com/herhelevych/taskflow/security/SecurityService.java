package com.herhelevych.taskflow.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Service("sec") // short alias
@RequiredArgsConstructor
public class SecurityService {

    private Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean hasProjectRole(UUID projectId, String role) {
        String authority = role + "_" + projectId.toString();
        Collection<? extends GrantedAuthority> authorities = getAuth().getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public boolean hasAnyProjectRole(UUID projectId, String... roles) {
        return Arrays.stream(roles)
                .anyMatch(role -> hasProjectRole(projectId, role));
    }

    public boolean isProjectAdminOrSuperadmin(UUID projectId) {
        return hasProjectRole(projectId, "ROLE_ADMIN") || isSuperadmin();
    }

    public boolean isSuperadmin() {
        return getAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
    }

}
