package com.herhelevych.taskflow.mappers;

import com.herhelevych.taskflow.domain.dtos.LoginRequest;
import com.herhelevych.taskflow.domain.dtos.RegisterRequest;
import com.herhelevych.taskflow.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.password()))")
    User toEntity(RegisterRequest request, PasswordEncoder passwordEncoder);
}
