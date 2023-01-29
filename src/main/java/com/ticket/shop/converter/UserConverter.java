package com.ticket.shop.converter;

import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.persistence.entity.UserEntity;

/**
 * User converter
 */
public class UserConverter {
    /**
     * From {@link CreateUserDto} to {@link UserEntity}
     * @param createUserDto {@link CreateUserDto}
     * @return {@link UserEntity}
     */
    public static UserEntity fromCreateUserDtoToUserEntity(CreateUserDto createUserDto) {
        return UserEntity.builder()
                .firstname(createUserDto.getFirstname())
                .lastname(createUserDto.getLastname())
                .email(createUserDto.getEmail())
                .encryptedPassword(createUserDto.getPassword())
                .roles(createUserDto.getRoles())
                .build();
    }

    /**
     * From {@link UserEntity} to {@link UserDetailsDto}
     * @param userEntity {@link UserEntity}
     * @return {@link UserDetailsDto}
     */
    public static UserDetailsDto fromUserEntityToUserDetailsDto(UserEntity userEntity) {
        return UserDetailsDto.builder()
                .userId(userEntity.getUserId())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .email(userEntity.getEmail())
                .build();
    }
}
