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
            Object principal = authentication.getPrincipal();
            
            // If the principal is an instance of User (the default user type in Spring Security)
            if (principal instanceof User) {
                User user = (User) principal;
                return "Username: " + user.getUsername() + ", Roles: " + user.getAuthorities();
            }
        }
        return "No user authenticated";
    }
}
