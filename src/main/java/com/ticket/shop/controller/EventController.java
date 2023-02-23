package com.ticket.shop.controller;

import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.EventServiceImp;
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

/**
 * REST controller responsible for event operations
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Events", description = "Event endpoints")
public class EventController {

    private static final Logger LOGGER = LogManager.getLogger(CompanyController.class);
    private final EventServiceImp eventServiceImp;

    public EventController(EventServiceImp eventServiceImp) {
        this.eventServiceImp = eventServiceImp;
    }

    /**
     * Create new event
     *
     * @param createEventDto new event data
     * @return {@link EventDetailsDto}
     */
    @PostMapping("/companies/{companyId}/events")
    @PreAuthorize("@authorized.hasRole('ADMIN') || " +
            "((@authorized.hasRole('COMPANY_ADMIN') || @authorized.hasRole('WORKER')) && @authorized.isOnCompany(#companyId))")
    @Operation(summary = "Registration", description = "Register new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = EventDetailsDto.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.COMPANY_NOT_FOUND + " || " + ErrorMessages.COUNTRY_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<EventDetailsDto> createEvent(@Valid @RequestBody CreateEventDto createEventDto,
                                                       @PathVariable Long companyId) {

        LOGGER.info("Request to create new event - {}", createEventDto);
        EventDetailsDto eventDetailsDto;
        try {
            eventDetailsDto = this.eventServiceImp.createEvent(createEventDto, companyId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created event - {}", createEventDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        LOGGER.info("Event created successfully. Retrieving created event with id {}", eventDetailsDto.getEventId());
        return new ResponseEntity<>(eventDetailsDto, HttpStatus.CREATED);
    }
}

