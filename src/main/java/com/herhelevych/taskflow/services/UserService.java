package com.herhelevych.taskflow.services;

import com.herhelevych.taskflow.domain.dtos.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID id);
    List<UserResponse> findAll();
    void deleteUser(UUID id);
}
