package com.herhelevych.taskflow.services;

import com.herhelevych.taskflow.domain.dtos.AuthResponse;
import com.herhelevych.taskflow.domain.dtos.LoginRequest;
import com.herhelevych.taskflow.domain.dtos.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
