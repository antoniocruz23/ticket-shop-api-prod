package com.ticket.shop.controller;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.command.event.EventDetailsWithCalendarIdsDto;
import com.ticket.shop.command.event.UpdateEventDto;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

import static org.springframework.http.HttpStatus.OK;

/**
 * REST controller responsible for event operations
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Events", description = "Event endpoints")
public class EventController {

    private static final Logger LOGGER = LogManager.getLogger(EventController.class);
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
    @Operation(summary = "Registration",
            description = "Register new event - Access only for users with 'COMPANY_ADMIN' or 'WORKER' roles and the logged in user company id needs to be the same as the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Operation",
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

    /**
     * Get event by id
     *
     * @param eventId event id
     * @return {@link EventDetailsWithCalendarIdsDto}
     */
    @GetMapping("/events/{eventId}")
    @Operation(summary = "Get event by id", description = "Get event by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = EventDetailsWithCalendarIdsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.EVENT_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<EventDetailsWithCalendarIdsDto> getEventById(@PathVariable Long eventId) {

        LOGGER.info("Request to get event with id {}", eventId);
        EventDetailsWithCalendarIdsDto eventDetailsDto;
        try {
            eventDetailsDto = this.eventServiceImp.getEventById(eventId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get event with id {}", eventId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved event with id {}", eventId);
        return new ResponseEntity<>(eventDetailsDto, OK);
    }

    /**
     * Get event list
     *
     * @param page page number
     * @param size page size
     * @return {@link Paginated<EventDetailsDto>} event list and Ok httpStatus
     */
    @GetMapping("/events")
    @Operation(summary = "Get event list with pagination", description = "Get event list with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = EventDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<Paginated<EventDetailsDto>> getEventList(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(required = false) Long companyId,
                                                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                   @RequestParam(required = false) Date date) {

        LOGGER.info("Request to get event list - page: {}, size: {}", page, size);
        Paginated<EventDetailsDto> eventList;
        try {
            eventList = this.eventServiceImp.getEventList(page, size, companyId, date);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get event list - page: {}, size: {}", page, size, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved event list");
        return new ResponseEntity<>(eventList, OK);
    }

    /**
     * Update event
     *
     * @param companyId the company id
     * @param eventId   the event id
     * @return {@link EventDetailsDto}
     */
    @PutMapping("/companies/{companyId}/events/{eventId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || " +
            "((@authorized.hasRole('COMPANY_ADMIN') || @authorized.hasRole('WORKER')) && @authorized.isOnCompany(#companyId))")
    @Operation(summary = "Update event",
            description = "Update event - Access only for users with 'COMPANY_ADMIN' or 'WORKER' roles and the logged in user company id needs to be the same as the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = EventDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<EventDetailsDto> updateEvent(@PathVariable Long companyId,
                                                       @PathVariable Long eventId,
                                                       @Valid @RequestBody UpdateEventDto updateEventDto) {

        LOGGER.info("Request to update event with id {} from company id {} - {}", eventId, companyId, updateEventDto);
        EventDetailsDto eventDetailsDto;
        try {
            eventDetailsDto = this.eventServiceImp.updateEvent(companyId, eventId, updateEventDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to update event with id {} from company id {} - {}", eventId, companyId, updateEventDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Event with id {} updated successfully. Retrieving updated event", eventId);
        return new ResponseEntity<>(eventDetailsDto, HttpStatus.OK);
    }
}

