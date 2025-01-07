package com.bookeryapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookeryapi.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final JwtUtil jwtUtil;

    public String getUsernameFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer")) {
            String token = header.substring(7);
            return jwtUtil.extractUsername(token);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
