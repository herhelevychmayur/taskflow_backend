package com.herhelevych.taskflow.services;

import com.herhelevych.taskflow.domain.dtos.UserResponse;
import com.herhelevych.taskflow.domain.entities.User;

    import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID id);
    List<UserResponse> findAll();
}
