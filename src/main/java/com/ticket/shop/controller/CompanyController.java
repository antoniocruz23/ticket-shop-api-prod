package com.ticket.shop.controller;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateCompanyDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
     * @param createCompanyDto new company data
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
    public ResponseEntity<CompanyDetailsDto> companyRegistration(@Valid @RequestBody CreateCompanyDto createCompanyDto) {

        LOGGER.info("Request to create new company - {}", createCompanyDto);
        CompanyDetailsDto companyDetailsDto;
        try {
            companyDetailsDto = this.companyService.createCompany(createCompanyDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created company - {}", createCompanyDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Company created successfully. Retrieving created company with id {}", companyDetailsDto.getCompanyId());
        return new ResponseEntity<>(companyDetailsDto, HttpStatus.CREATED);
    }
}
