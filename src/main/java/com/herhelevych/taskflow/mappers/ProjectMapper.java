package com.herhelevych.taskflow.mappers;

import com.herhelevych.taskflow.domain.dtos.*;
import com.herhelevych.taskflow.domain.entities.Project;
import com.herhelevych.taskflow.domain.entities.ProjectInvite;
import com.herhelevych.taskflow.domain.entities.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toEntity(ProjectCreateRequest request);
    ProjectResponse toResponse(Project project);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "username", source = "user.username")
    ProjectMemberResponse toMemberResponse(ProjectMember savedMember);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "inviterId", source = "inviter.id")
    @Mapping(target = "inviterFullName", source = "inviter.fullName")
    @Mapping(target = "inviteeId", source = "invitee.id")
    @Mapping(target = "inviteeFullName", source = "invitee.fullName")
    ProjectMemberInviteResponse toInviteResponse(ProjectInvite savedMember);

    ProjectShortResponse toShortResponse(Project project);
}
