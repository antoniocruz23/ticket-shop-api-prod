package com.ticket.shop.service;

import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UpdateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.command.user.WorkerDetailsDto;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;

/**
 * Common interface for user services, provides methods to manage users
 */
public interface UserService {

    /**
     * Create new customer
     *
     * @param createUserDto {@link CreateUserDto}
     * @return {@link UserDetailsDto} the user created
     * @throws UserAlreadyExistsException when the user already exists
     */
    UserDetailsDto createCustomer(CreateUserDto createUserDto) throws UserAlreadyExistsException;

    /**
     * Create new worker
     *
     * @param createUserDto {@link CreateUserDto}
     * @param userId        company admin id
     * @return {@link UserDetailsDto} the user created
     * @throws UserAlreadyExistsException when the user already exists
     */
    UserDetailsDto createWorker(CreateUserDto createUserDto, Long userId) throws UserAlreadyExistsException;

    /**
     * Get user by id
     *
     * @param userId user id to be got
     * @return {@link UserDetailsDto} the user obtained
     * @throws UserNotFoundException when the user isn't found
     */
    UserDetailsDto getUserById(Long userId) throws UserNotFoundException;

    /**
     * Update user
     *
     * @param userId        user id to be updated
     * @param updateUserDto {@link UpdateUserDto}
     * @return {@link UserDetailsDto} the user updated
     * @throws UserNotFoundException when the user isn't found
     */
    UserDetailsDto updateUser(Long userId, UpdateUserDto updateUserDto) throws UserNotFoundException;

    /**
     * Get worker by id
     * Only can get a worker from the same company as the userId company
     *
     * @param workerId  worker id to be got
     * @param companyId company id
     * @return {@link WorkerDetailsDto} the worker obtained
     * @throws UserNotFoundException when the user isn't found
     */
    WorkerDetailsDto getWorkerById(Long workerId, Long companyId) throws UserNotFoundException;
}
