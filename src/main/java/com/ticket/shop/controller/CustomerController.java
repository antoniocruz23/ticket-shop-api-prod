package com.ticket.shop.controller;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.command.customer.UpdateCustomerDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.persistence.entity.UserEntity;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final CustomerServiceImp customerServiceImp;

    public CustomerController(CustomerServiceImp customerServiceImp) {
        this.customerServiceImp = customerServiceImp;
    }

    /**
     * Create new costumer
     *
     * @param createCustomerDto new customer data
     * @return {@link CustomerDetailsDto}
     */
    @PostMapping()
    @Operation(summary = "Registration", description = "Register new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.COUNTRY_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = ErrorMessages.EMAIL_ALREADY_EXISTS,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CustomerDetailsDto> customerRegistration(@Valid @RequestBody CreateCustomerDto createCustomerDto) {

        LOGGER.info("Request to create new customer - {}", createCustomerDto);
        CustomerDetailsDto customerDetailsDto;
        try {
            customerDetailsDto = this.customerServiceImp.createCustomer(createCustomerDto);

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
     * @param customerId customer id
     * @return {@link CustomerDetailsDto} the customer wanted and Ok httpStatus
     */
    @GetMapping("/{customerId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || (@authorized.hasRole('CUSTOMER') && @authorized.isUser(#customerId))")
    @Operation(summary = "Get customer",
            description = "Get customer by id - Restrict for users with 'CUSTOMER' role and the logged in user id needs to be the same as the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CustomerDetailsDto> getCustomerById(@PathVariable Long customerId) {

        LOGGER.info("Request to get customer with id {}", customerId);
        CustomerDetailsDto customerDetailsDto;
        try {
            customerDetailsDto = this.customerServiceImp.getCustomerById(customerId);

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
     * Get customers list
     *
     * @param page page number
     * @param size page size
     * @return {@link Paginated<CustomerDetailsDto>} customers list wanted and Ok httpStatus
     */
    @GetMapping()
    @PreAuthorize("@authorized.hasRole('ADMIN')")
    @Operation(summary = "Get customers by pagination", description = "Get customers by pagination - Access only for application Admins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = Paginated.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<Paginated<CustomerDetailsDto>> getCustomersList(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {

        LOGGER.info("Request to get customers list - page: {}, size: {}", page, size);
        Paginated<CustomerDetailsDto> customersList;
        try {
            customersList = this.customerServiceImp.getCustomersList(page, size);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get customers list - ", e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieving customers list");
        return new ResponseEntity<>(customersList, HttpStatus.OK);
    }

    /**
     * Update customer
     *
     * @param customerId        the customer id
     * @param updateCustomerDto data to update
     * @return {@link CustomerDetailsDto}
     */
    @PutMapping("/{customerId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || (@authorized.hasRole('CUSTOMER') && @authorized.isUser(#customerId))")
    @Operation(summary = "Update customer",
            description = "Update customer - Restrict for users with 'CUSTOMER' role and the logged in user id needs to be the same as the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND + " || " + ErrorMessages.COUNTRY_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CustomerDetailsDto> updateCustomer(@PathVariable Long customerId,
                                                             @Valid @RequestBody UpdateCustomerDto updateCustomerDto) {

        LOGGER.info("Request to update customer with id {} - {}", customerId, updateCustomerDto);
        CustomerDetailsDto customerDetailsDto;
        try {
            customerDetailsDto = this.customerServiceImp.updateCustomer(customerId, updateCustomerDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to update customer with id {} - {}", customerId, updateCustomerDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Customer with id {} updated successfully. Retrieving updated customer", customerId);
        return new ResponseEntity<>(customerDetailsDto, HttpStatus.OK);
    }

    /**
     * Delete Customer
     *
     * @param customerId customer id
     */
    @DeleteMapping("/{customerId}")
    @PreAuthorize("@authorized.hasRole(\"ADMIN\")")
    @Operation(summary = "Delete Customer", description = "Delete Customer - Access only for application Admins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {

        LOGGER.info("Request to delete customer with id - {}", customerId);
        try {
            this.customerServiceImp.deleteCustomer(customerId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to delete customer with id {}", customerId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Customer with id {} deleted successfully", customerId);
        return ResponseEntity.noContent().build();
    }
}
