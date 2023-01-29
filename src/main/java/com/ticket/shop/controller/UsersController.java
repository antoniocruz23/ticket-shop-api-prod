package com.ticket.shop.controller;

import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.persistence.entity.UserEntity;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * REST controller responsible for {@link UserEntity}
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Users Controller")
public class UsersController {

    private static final Logger LOGGER = LogManager.getLogger(UsersController.class);

    private final UserServiceImp userServiceImp;

    public UsersController(UserServiceImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    /**
     * Create new user
     *
     * @param createUserDto new user data
     * @return the response entity
     */
    @PostMapping
    @Operation(summary = "Registration", description = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<UserDetailsDto> usersRegistration(@Valid @RequestBody CreateUserDto createUserDto) {
        LOGGER.info("Request to create new user - {}", createUserDto);

        UserDetailsDto usersDetailsDto;
        try {
            usersDetailsDto = userServiceImp.createUser(createUserDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created user - {}", createUserDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("User created successfully. Retrieving created user with id {}", usersDetailsDto.getUserId());
        return new ResponseEntity<>(usersDetailsDto, HttpStatus.CREATED);
    }
}
