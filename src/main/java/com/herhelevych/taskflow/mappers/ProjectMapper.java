package com.herhelevych.taskflow.mappers;

import com.herhelevych.taskflow.domain.dtos.*;
import com.herhelevych.taskflow.domain.entities.Project;
import com.herhelevych.taskflow.domain.entities.ProjectInvite;
import com.herhelevych.taskflow.domain.entities.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toEntity(ProjectCreateRequest request);
    ProjectResponse toResponse(Project project);

    ProjectMemberResponse toMemberResponse(ProjectMember savedMember);

    ProjectMemberInviteResponse toInviteResponse(ProjectInvite savedMember);

    ProjectShortResponse toShortResponse(Project project);
}
