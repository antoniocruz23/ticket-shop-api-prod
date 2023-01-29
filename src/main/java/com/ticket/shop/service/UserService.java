package com.ticket.shop.service;

import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;

/**
 * Common interface for user services, provides methods to manage users
 */
public interface UserService {

    /**
     * Create new user
     *
     * @param createUserDto {@link CreateUserDto}
     * @return {@link UserDetailsDto} the user created
     * @throws UserAlreadyExistsException when the user already exists
     */
    UserDetailsDto createUser(CreateUserDto createUserDto) throws UserAlreadyExistsException;

    /**
     * Get user by id
     *
     * @param userId user id to be got
     * @return {@link UserDetailsDto} the user obtained
     * @throws UserNotFoundException when the user isn't found
     */
    UserDetailsDto getUserById(long userId) throws UserNotFoundException;
}
