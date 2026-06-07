package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.dtos.AuthResponse;
import com.herhelevych.taskflow.domain.dtos.LoginRequest;
import com.herhelevych.taskflow.domain.dtos.RegisterRequest;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.UserMapper;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.services.AuthService;
import com.herhelevych.taskflow.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.username())){
            throw new IllegalArgumentException("User already exist with username " + request.username());
        }
        User user = userMapper.toEntity(request, passwordEncoder);
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getUsername()));
        return new AuthResponse(jwtToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var userDetails = userDetailsService.loadUserByUsername(request.username());
        var jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponse(jwtToken);
    }
}
