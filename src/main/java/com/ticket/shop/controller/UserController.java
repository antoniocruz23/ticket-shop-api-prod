package com.ticket.shop.controller;

import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UpdateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.command.user.WorkerDetailsDto;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.service.UserService;
import com.ticket.shop.service.UserServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;


/**
 * REST controller responsible for {@link UserEntity}
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Users", description = "Users API")
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserServiceImp userService) {
        this.userService = userService;
    }

    /**
     * Create new costumer
     *
     * @param createUserDto new user data
     * @return the response entity
     */
    @PostMapping("/customers")
    @Operation(summary = "Registration", description = "Register new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<UserDetailsDto> customerRegistration(@Valid @RequestBody CreateUserDto createUserDto) {

        LOGGER.info("Request to create new customer - {}", createUserDto);
        UserDetailsDto usersDetailsDto;
        try {
            usersDetailsDto = this.userService.createCustomer(createUserDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created customer - {}", createUserDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Customer created successfully. Retrieving created customer with id {}", usersDetailsDto.getUserId());
        return new ResponseEntity<>(usersDetailsDto, HttpStatus.CREATED);
    }

    /**
     * Create new worker
     *
     * @param createUserDto new user data
     * @return the response entity
     */
    @PostMapping("/companies/admins/{userId}/workers")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.hasRole('COMPANY_ADMIN')")
    @Operation(summary = "Registration", description = "Register new worker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<UserDetailsDto> workerRegistration(@Valid @RequestBody CreateUserDto createUserDto,
                                                             @PathVariable Long userId) {

        LOGGER.info("Request to create new user - {}", createUserDto);
        UserDetailsDto usersDetailsDto;
        try {
            usersDetailsDto = this.userService.createWorker(createUserDto, userId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created worker - {}", createUserDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Worker created successfully. Retrieving created worker with id {}", usersDetailsDto.getUserId());
        return new ResponseEntity<>(usersDetailsDto, HttpStatus.CREATED);
    }

    /**
     * Get user by id
     *
     * @param userId user id
     * @return {@link UserDetailsDto} the user wanted and Ok httpStatus
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.isUser(#userId)")
    @Operation(summary = "Get user", description = "Get user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<UserDetailsDto> getUserById(@PathVariable Long userId) {

        LOGGER.info("Request to get user with id {}", userId);
        UserDetailsDto usersDetailsDto;
        try {
            usersDetailsDto = this.userService.getUserById(userId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get user with id {}", userId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved user with id {}", userId);
        return new ResponseEntity<>(usersDetailsDto, OK);
    }

    /**
     * Update user
     *
     * @param userId        the user id
     * @param updateUserDto data to update
     * @return the response entity
     */
    @PutMapping("/users/{userId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.isUser(#userId)")
    @Operation(summary = "Update user", description = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<UserDetailsDto> updateUser(@PathVariable Long userId,
                                                     @Valid @RequestBody UpdateUserDto updateUserDto) {

        LOGGER.info("Request to update user with id {} - {}", userId, updateUserDto);
        UserDetailsDto userDetailsDto;
        try {
            userDetailsDto = this.userService.updateUser(userId, updateUserDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to update user with id {} - {}", userId, updateUserDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("User with id {} updated successfully. Retrieving updated user", userId);
        return new ResponseEntity<>(userDetailsDto, HttpStatus.OK);
    }

    /**
     * Get worker by company id and worker id
     *
     * @param companyId company id
     * @param workerId  worker id
     * @return {@link WorkerDetailsDto} the worker wanted and Ok httpStatus
     */
    @GetMapping("/companies/{companyId}/workers/{workerId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.hasRole('COMPANY_ADMIN')")
    @Operation(summary = "Get worker by company", description = "Get worker by company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<WorkerDetailsDto> getWorkerById(@PathVariable Long companyId,
                                                          @PathVariable Long workerId) {

        LOGGER.info("Request to get worker with id {}", workerId);
        WorkerDetailsDto usersDetailsDto;
        try {
            usersDetailsDto = this.userService.getWorkerById(workerId, companyId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get worker with id {}", workerId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved worker with id {}", workerId);
        return new ResponseEntity<>(usersDetailsDto, OK);
    }
}
