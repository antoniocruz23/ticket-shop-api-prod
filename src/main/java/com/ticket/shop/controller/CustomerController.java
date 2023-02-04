package com.ticket.shop.controller;

import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.command.customer.UpdateCustomerDto;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.service.CustomerService;
import com.ticket.shop.service.CustomerServiceImp;
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
 * REST controller responsible for some operations of {@link UserEntity} related to Customers
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer endpoints")
public class CustomerController {

    private static final Logger LOGGER = LogManager.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerServiceImp userService) {
        this.customerService = userService;
    }

    /**
     * Create new costumer
     *
     * @param createCustomerDto new customer data
     * @return the response entity
     */
    @PostMapping()
    @Operation(summary = "Registration", description = "Register new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CustomerDetailsDto> customerRegistration(@Valid @RequestBody CreateCustomerDto createCustomerDto) {

        LOGGER.info("Request to create new customer - {}", createCustomerDto);
        CustomerDetailsDto customerDetailsDto;
        try {
            customerDetailsDto = this.customerService.createCustomer(createCustomerDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created customer - {}", createCustomerDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Customer created successfully. Retrieving created customer with id {}", customerDetailsDto.getUserId());
        return new ResponseEntity<>(customerDetailsDto, HttpStatus.CREATED);
    }

    /**
     * Get customer by id
     *
     * @param customerId user id
     * @return {@link CustomerDetailsDto} the customer wanted and Ok httpStatus
     */
    @GetMapping("/{customerId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.isUser(#customerId)")
    @Operation(summary = "Get customer", description = "Get customer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CustomerDetailsDto> getUserById(@PathVariable Long customerId) {

        LOGGER.info("Request to get customer with id {}", customerId);
        CustomerDetailsDto customerDetailsDto;
        try {
            customerDetailsDto = this.customerService.getCustomerById(customerId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get customer with id {}", customerId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved customer with id {}", customerId);
        return new ResponseEntity<>(customerDetailsDto, OK);
    }

    /**
     * Update customer
     *
     * @param customerId        the customer id
     * @param updateCustomerDto data to update
     * @return the response entity
     */
    @PutMapping("/{customerId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.isUser(#customerId)")
    @Operation(summary = "Update user", description = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CustomerDetailsDto> updateCustomer(@PathVariable Long customerId,
                                                             @Valid @RequestBody UpdateCustomerDto updateCustomerDto) {

        LOGGER.info("Request to update customer with id {} - {}", customerId, updateCustomerDto);
        CustomerDetailsDto customerDetailsDto;
        try {
            customerDetailsDto = this.customerService.updateCustomer(customerId, updateCustomerDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to update customer with id {} - {}", customerId, updateCustomerDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Customer with id {} updated successfully. Retrieving updated customer", customerId);
        return new ResponseEntity<>(customerDetailsDto, HttpStatus.OK);
    }
}
