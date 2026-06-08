package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.dtos.AuthResponse;
import com.herhelevych.taskflow.domain.dtos.LoginRequest;
import com.herhelevych.taskflow.domain.dtos.RegisterRequest;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.UserMapper;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.security.UserDetailsImpl;
import com.herhelevych.taskflow.services.AuthService;
import com.herhelevych.taskflow.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.username())){
            throw new IllegalArgumentException("User already exist with username " + request.username());
        }
        User user = userMapper.toEntity(request, passwordEncoder);
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getUsername()), user.getId());
        return new AuthResponse(jwtToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        var userDetails =(UserDetailsImpl) userDetailsService.loadUserByUsername(request.username());
        var jwtToken = jwtService.generateToken(userDetails, userDetails.getId());
        return new AuthResponse(jwtToken);
    }
}
