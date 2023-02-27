package com.ticket.shop.controller;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsWhenCreatedDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.TicketServiceImp;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller responsible for ticket operations
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Tickets", description = "Ticket endpoints")
public class TicketController {

    private static final Logger LOGGER = LogManager.getLogger(TicketController.class);
    private final TicketServiceImp ticketServiceImp;

    public TicketController(TicketServiceImp ticketServiceImp) {
        this.ticketServiceImp = ticketServiceImp;
    }

    @PostMapping("/companies/{companyId}/calendars/{calendarId}/tickets")
    @PreAuthorize("@authorized.hasRole('ADMIN') || ((@authorized.hasRole('COMPANY_ADMIN') || @authorized.hasRole('WORKER')) && @authorized.isOnCompany(#companyId))")
    @Operation(summary = "Create new tickets", description = "Create tickets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = TicketDetailsWhenCreatedDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.COMPANY_NOT_FOUND + " || " + ErrorMessages.CALENDAR_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "422", description = ErrorMessages.INVALID_TICKET_TYPE,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<List<TicketDetailsWhenCreatedDto>> createListOfTickets(@PathVariable Long companyId,
                                                                                 @PathVariable Long calendarId,
                                                                                 @Valid @RequestBody List<CreateTicketDto> createTicketDtoList) {

        LOGGER.info("Request to create ticket list - {}", createTicketDtoList);
        List<TicketDetailsWhenCreatedDto> ticketDetailsDtoList;
        try {
            ticketDetailsDtoList = this.ticketServiceImp.bulkCreateTicket(companyId, calendarId, createTicketDtoList);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created ticket list for calendar id - {}", calendarId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Ticket list created successfully. Retrieving created ticket list for calendar id {}", calendarId);
        return new ResponseEntity<>(ticketDetailsDtoList, HttpStatus.CREATED);
    }
}
