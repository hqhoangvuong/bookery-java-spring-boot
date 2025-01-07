package com.bookeryapi.dto;

import java.time.Instant;

public record LoginResponseDto(
        String token,
        String tokenType,
        Instant expiration) {

}