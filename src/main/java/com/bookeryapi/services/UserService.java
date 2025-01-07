package com.bookeryapi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bookeryapi.dto.UserDto;
import com.bookeryapi.dto.UserPasswordChangeRequestDto;
import com.bookeryapi.dto.UserRegistryRequestDto;
import com.bookeryapi.mappers.UserMapper;

import lombok.RequiredArgsConstructor;
import org.jooq.bookery.default_.public_.tables.Users;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class UserService {
    private final DSLContext dslContext;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserMapper userMapper;

    public UserDto getUserById (String username) {
        var userRecord = dslContext
            .selectFrom(Users.USERS)
            .where(Users.USERS.USERNAME.eq(username))
            .fetchOne();

        return userMapper.toUserDto(userRecord);
    }

    public List<UserDto> getAllUsers() {
        var userRecords = dslContext
            .selectFrom(Users.USERS)
            .fetch();

        return userMapper.toUserDtos(userRecords);
    }

    public boolean createUser(UserRegistryRequestDto userRegistryRequest) {
        String encodedPassword = passwordEncoder.encode(userRegistryRequest.Password());

        var result = dslContext
            .insertInto(Users.USERS)
            .set(Users.USERS.USER_ID, UUID.randomUUID().toString())
            .set(Users.USERS.USERNAME, userRegistryRequest.Username())
            .set(Users.USERS.PASSWORD, encodedPassword)
            .set(Users.USERS.FIRSTNAME, userRegistryRequest.FirstName())
            .set(Users.USERS.LASTNAME, userRegistryRequest.LastName())
            .set(Users.USERS.EMAIL, userRegistryRequest.Email())
            .set(Users.USERS.IS_ACTIVE, true)
            .set(Users.USERS.IS_ADMIN, 0)
            .set(Users.USERS.USER_CREATE_DATE, LocalDateTime.now())
            .execute();

        log.info("Inserted with result: {}", result);

        return result == 1;
    }

    public boolean changePassword(
        String username,
        UserPasswordChangeRequestDto userPasswordChangeRequestDto
    ) {
        var userRecord = dslContext
            .selectFrom(Users.USERS)
            .where(Users.USERS.USERNAME.eq(username))
            .fetchOne();

        if (userRecord == null) {
            return false;
        }

        if (!passwordEncoder.matches(
            userPasswordChangeRequestDto.oldPassword(),
            userRecord.getPassword()
        )) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(userPasswordChangeRequestDto.newPassword());

        var result = dslContext
            .update(Users.USERS)
            .set(Users.USERS.PASSWORD, encodedPassword)
            .where(Users.USERS.USERNAME.eq(username))
            .execute();

        log.info("Updated with result: {}", result);

        return result == 1;
    }
}
