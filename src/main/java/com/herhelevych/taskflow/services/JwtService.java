package com.herhelevych.taskflow.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
}
