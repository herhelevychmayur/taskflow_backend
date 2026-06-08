package com.herhelevych.taskflow.mappers;

import com.herhelevych.taskflow.domain.dtos.CommentResponse;
import com.herhelevych.taskflow.domain.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorFullName", source = "user.fullName")
    CommentResponse toResponse(Comment comment);
}
