package com.herhelevych.taskflow.aspect;

import com.herhelevych.taskflow.repositories.ProjectInviteRepository;
import com.herhelevych.taskflow.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class ArchivedProjectAspect {

    private final ProjectRepository projectRepository;
    private final ProjectInviteRepository projectInviteRepository;

    @Before("@annotation(preventIfArchived)")
    public void checkProjectArchived(JoinPoint joinPoint, PreventIfArchived preventIfArchived) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        String idParamName = preventIfArchived.idParam();
        UUID targetId = null;

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                if (parameterNames[i].equals(idParamName) && args[i] instanceof UUID) {
                    targetId = (UUID) args[i];
                    break;
                }
            }
        }

        // Fallback: if parameterNames are not preserved by the compiler,
        // search for the first UUID parameter in the list.
        if (targetId == null) {
            for (Object arg : args) {
                if (arg instanceof UUID) {
                    targetId = (UUID) arg;
                    break;
                }
            }
        }

        if (targetId == null) {
            throw new IllegalArgumentException("Could not find UUID parameter named: " + idParamName);
        }

        boolean isArchived = false;

        switch (preventIfArchived.type()) {
            case PROJECT:
                isArchived = projectRepository.findById(targetId)
                        .map(com.herhelevych.taskflow.domain.entities.Project::isArchived)
                        .orElseThrow(() -> new EntityNotFoundException("Project not found"));
                break;
            case INVITE:
                isArchived = projectInviteRepository.findById(targetId)
                        .map(invite -> invite.getProject().isArchived())
                        .orElseThrow(() -> new EntityNotFoundException("Invite not found"));
                break;
        }

        if (isArchived) {
            throw new IllegalStateException("Cannot perform this action because the project is archived");
        }
    }
}
