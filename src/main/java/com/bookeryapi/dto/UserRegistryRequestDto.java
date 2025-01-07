package com.bookeryapi.dto;

public record UserRegistryRequestDto(
                String Username,
                String Password,
                String Email,
                String FirstName,
                String LastName) {

}
