package com.bookeryapi.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.bookeryapi.dto.UserDto;
import com.bookeryapi.dto.UserPasswordChangeRequestDto;
import com.bookeryapi.services.AuthService;
import com.bookeryapi.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "Operations with User")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping()
    public UserDto getUser(HttpServletRequest request) {
        String username = authService.getUsernameFromRequest(request);

        return userService.getUserById(username);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody UserPasswordChangeRequestDto userPasswordChangeRequestDto,
            HttpServletRequest request) {
        String username = authService.getUsernameFromRequest(request);

        if (userService.changePassword(username, userPasswordChangeRequestDto)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public String getMethodName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            System.out.println("Authentication: " + authentication);

            Object authorities = authentication.getAuthorities();
        }
        return "No user authenticated";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminAccess() {
        return "This is an admin-only endpoint.";
    }
}
