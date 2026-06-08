package com.herhelevych.taskflow.mappers;

import com.herhelevych.taskflow.domain.dtos.TaskResponse;
import com.herhelevych.taskflow.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "assigneeFullName", source = "assignee.fullName")
    @Mapping(target = "assigneeUsername", source = "assignee.username")
    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "creatorFullName", source = "creator.fullName")
    @Mapping(target = "creatorUsername", source = "creator.username")
    TaskResponse toResponse(Task task);
}
