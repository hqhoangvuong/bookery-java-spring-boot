package com.bookeryapi.utils;

import org.jooq.DSLContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.jooq.bookery.default_.public_.tables.Users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    // Inject your DSLContext or Repository for DB access
    private final DSLContext dslContext;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Query user from the database
        var userRecord = dslContext
                .selectFrom(Users.USERS)
                .where(Users.USERS.USERNAME.eq(username))
                .fetchOne();

        if (userRecord == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Verify password (ensure you use an encoder like BCrypt)
        if (!new BCryptPasswordEncoder().matches(password, userRecord.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var userRole = userRecord.getIsAdmin() == 1 ? "ADMIN" : "USER";

        UserDetails userDetails = User
                .builder()
                .username(userRecord.getUsername())
                .password(userRecord.getPassword())
                .roles(userRole)
                .build();

        // Return authenticated token
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
