package com.ticket.shop.controller;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.AddressServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

/**
 * REST controller responsible for address operations
 */
@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Addresses", description = "Address endpoints")
public class AddressController {

    private static final Logger LOGGER = LogManager.getLogger(CustomerController.class);
    private final AddressServiceImp addressServiceImp;

    public AddressController(AddressServiceImp addressServiceImp) {
        this.addressServiceImp = addressServiceImp;
    }

    /**
     * Create address for a user
     *
     * @param userId           user id
     * @param createAddressDto {@link CreateAddressDto}
     * @return {@link AddressDetailsDto}
     */
    @PostMapping("/users/{userId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || " +
            "((@authorized.hasRole('CUSTOMER') || @authorized.hasRole('WORKER')) && @authorized.isUser(#userId))")
    @Operation(summary = "Create User Address",
            description = "Create new address - Access only for the own user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.COUNTRY_NOT_FOUND + " || " + ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<AddressDetailsDto> createUserAddress(@PathVariable Long userId,
                                                               @Valid @RequestBody CreateAddressDto createAddressDto) {

        LOGGER.info("Request to create address for user with id - {}", userId);
        AddressDetailsDto addressDetailsDto;
        try {
            addressDetailsDto = this.addressServiceImp.createUserAddress(userId, createAddressDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to create address {} for user with id {}", createAddressDto, userId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Address for user with id {} created successfully", userId);
        return new ResponseEntity<>(addressDetailsDto, OK);
    }
}
