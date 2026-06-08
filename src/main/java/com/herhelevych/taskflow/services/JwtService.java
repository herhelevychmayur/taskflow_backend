package com.herhelevych.taskflow.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {
    String generateToken(UserDetails userDetails, UUID userId);
    String extractUsername(String token);
}
