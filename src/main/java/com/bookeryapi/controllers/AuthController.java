package com.bookeryapi.controllers;

import java.time.Instant;

import org.jooq.bookery.default_.public_.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.bookeryapi.dto.LoginResponseDto;
import com.bookeryapi.dto.UserRegistryRequestDto;
import com.bookeryapi.services.UserService;
import com.bookeryapi.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final UserService userService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid principal type");
        }

        User user = (User) authentication.getPrincipal();

        var expiration = jwtUtil.getExpiration();
        var token = jwtUtil.generateToken(
                user.getUsername(),
                user.getAuthorities());

        return new LoginResponseDto(
                token,
                "Bearer",
                expiration);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegistryRequestDto userRegistryRequest) {
        userService.createUser(userRegistryRequest);

        return ResponseEntity.noContent().build();
    }
}
