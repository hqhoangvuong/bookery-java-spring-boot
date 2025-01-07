package com.bookeryapi.dto;

public record UserPasswordChangeRequestDto(
        String oldPassword,
        String newPassword) {
}
