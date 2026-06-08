package com.herhelevych.taskflow.services.impl;

import com.herhelevych.taskflow.domain.dtos.UserResponse;
import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.mappers.UserMapper;
import com.herhelevych.taskflow.repositories.UserRepository;
import com.herhelevych.taskflow.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse findById(UUID id) {
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).collect(Collectors.toList());
    }
}
