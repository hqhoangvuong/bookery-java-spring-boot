package com.bookeryapi.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bookeryapi.dto.UserDto;
import com.bookeryapi.dto.UserPasswordChangeRequestDto;
import com.bookeryapi.services.AuthService;
import com.bookeryapi.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {

        String username = authService.getUsernameFromRequest(request);
        try (InputStream inputStream = file.getInputStream()) {
            userService.updateUserAvatar(username, inputStream);
            return ResponseEntity.ok("Avatar image updated successfully.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image.");
        }
    }

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar(HttpServletRequest request) {
        String username = authService.getUsernameFromRequest(request);

        try {
            byte[] imageData = userService.getUserAvatarImage(username);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<UserDto> GetAllUsers() {
        return userService.getAllUsers();
    }
}
