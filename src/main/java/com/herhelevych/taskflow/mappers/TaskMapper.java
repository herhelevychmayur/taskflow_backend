package com.herhelevych.taskflow.mappers;

import com.herhelevych.taskflow.domain.dtos.TaskResponse;
import com.herhelevych.taskflow.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "assigneeId", source = "assignee_id.id")
    @Mapping(target = "assigneeFullName", source = "assignee_id.fullName")
    @Mapping(target = "creatorId", source = "creator_id.id")
    @Mapping(target = "creatorFullName", source = "creator_id.fullName")
    TaskResponse toResponse(Task task);
}
