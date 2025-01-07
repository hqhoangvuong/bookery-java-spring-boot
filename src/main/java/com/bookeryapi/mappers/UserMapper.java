package com.bookeryapi.mappers;
import java.util.List;

import org.jooq.Result;
import org.jooq.bookery.default_.public_.tables.records.UsersRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookeryapi.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "firstname", target = "firstName")
    @Mapping(source = "lastname", target = "lastName")
    UserDto toUserDto(UsersRecord user);

    List<UserDto> toUserDtos(Result<UsersRecord> userRecords);
}