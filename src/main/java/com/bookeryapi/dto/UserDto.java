package com.bookeryapi.dto;

public record UserDto(
        String username,
        String email,
        String firstName,
        String lastName,
        String about,
        String country,
        String city,
        int isAdmin) {
}