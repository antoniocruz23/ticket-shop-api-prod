package com.ticket.shop.controller;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateAndUpdateCompanyDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.CompanyService;
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
 * REST controller responsible for company operations
 */
@RestController
@RequestMapping("/api/companies")
@Tag(name = "Companies", description = "Company endpoints")
public class CompanyController {

    private static final Logger LOGGER = LogManager.getLogger(CustomerController.class);
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Create new company
     *
     * @param createAndUpdateCompanyDto new company data
     * @return {@link CompanyDetailsDto} the response entity
     */
    @PostMapping()
    @PreAuthorize("@authorized.hasRole('ADMIN')")
    @Operation(summary = "Registration", description = "Register new company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = CompanyDetailsDto.class))),
            @ApiResponse(responseCode = "409", description = "Company already exists",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CompanyDetailsDto> companyRegistration(@Valid @RequestBody CreateAndUpdateCompanyDto createAndUpdateCompanyDto) {

        LOGGER.info("Request to create new company - {}", createAndUpdateCompanyDto);
        CompanyDetailsDto companyDetailsDto;
        try {
            companyDetailsDto = this.companyService.createCompany(createAndUpdateCompanyDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created company - {}", createAndUpdateCompanyDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Company created successfully. Retrieving created company with id {}", companyDetailsDto.getCompanyId());
        return new ResponseEntity<>(companyDetailsDto, HttpStatus.CREATED);
    }

    /**
     * Get company by id
     *
     * @param companyId company id
     * @return {@link CompanyDetailsDto} the company wanted and Ok httpStatus
     */
    @GetMapping("/{companyId}")
    @Operation(summary = "Get company by id", description = "Get company by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CompanyDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.COMPANY_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CompanyDetailsDto> getCompanyById(@PathVariable Long companyId) {

        LOGGER.info("Request to get company with id {}", companyId);
        CompanyDetailsDto companyDetailsDto;
        try {
            companyDetailsDto = this.companyService.getCompanyById(companyId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get company with id {}", companyId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved company with id {}", companyId);
        return new ResponseEntity<>(companyDetailsDto, OK);
    }

    /**
     * Update company
     *
     * @param companyId the company id
     * @return the response entity
     */
    @PutMapping("/{companyId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || (@authorized.hasRole('COMPANY_ADMIN') && @authorized.isOnCompany(#companyId))")
    @Operation(summary = "Update company", description = "Update company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.COMPANY_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CompanyDetailsDto> updateCompany(@PathVariable Long companyId,
                                                           @Valid @RequestBody CreateAndUpdateCompanyDto updateCompanyDto) {

        LOGGER.info("Request to update company with id {} - {}", companyId, updateCompanyDto);
        CompanyDetailsDto companyDetailsDto;
        try {
            companyDetailsDto = this.companyService.updateCompany(companyId, updateCompanyDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to update company with id {} - {}", companyId, updateCompanyDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Company with id {} updated successfully. Retrieving updated company", companyId);
        return new ResponseEntity<>(companyDetailsDto, HttpStatus.OK);
    }
}
