package com.ticket.shop.controller;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CalendarDetailsWithTicketsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.CalendarServiceImp;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

/**
 * REST controller responsible for calendar operations
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Calendars", description = "Calendar endpoints")
public class CalendarController {

    private static final Logger LOGGER = LogManager.getLogger(CalendarController.class);
    private final CalendarServiceImp calendarServiceImp;

    public CalendarController(CalendarServiceImp calendarServiceImp) {
        this.calendarServiceImp = calendarServiceImp;
    }

    /**
     * Create new calendar
     *
     * @param createCalendarDto new calendar data
     * @param companyId         company id
     * @param eventId           event id
     * @return the response entity
     */
    @PostMapping("/companies/{companyId}/events/{eventId}/calendars")
    @PreAuthorize("@authorized.hasRole('ADMIN') || " +
            "((@authorized.hasRole('COMPANY_ADMIN') || @authorized.hasRole('WORKER')) && @authorized.isOnCompany(#companyId))")
    @Operation(summary = "Create new calendar",
            description = "Create new calendar - Access only for users with 'COMPANY_ADMIN' or 'WORKER' roles and the logged-in user company id needs to be the same as the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CalendarDetailsWithTicketsDto.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.EVENT_NOT_FOUND + " || " + ErrorMessages.COMPANY_NOT_FOUND + " || " +
                    ErrorMessages.CALENDAR_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "422", description = ErrorMessages.INVALID_TICKET_TYPE,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CalendarDetailsWithTicketsDto> createCalendar(@Valid @RequestBody CreateCalendarDto createCalendarDto,
                                                                        @PathVariable Long companyId,
                                                                        @PathVariable Long eventId) {

        LOGGER.info("Request to create new calendar - {}", createCalendarDto);
        CalendarDetailsWithTicketsDto calendarDetailsDto;
        try {
            calendarDetailsDto = this.calendarServiceImp.createCalendar(createCalendarDto, companyId, eventId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created calendar - {}", createCalendarDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        LOGGER.info("Calendar created successfully. Retrieving created calendar with id {}", calendarDetailsDto.getCalendarId());
        return new ResponseEntity<>(calendarDetailsDto, HttpStatus.CREATED);
    }

    /**
     * Get calendar by id
     *
     * @param calendarId calendar id
     * @return {@link CalendarDetailsDto} the calendar wanted and Ok httpStatus
     */
    @GetMapping("/calendars/{calendarId}")
    @Operation(summary = "Get calendar by id", description = "Get calendar by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CalendarDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.CALENDAR_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CalendarDetailsDto> getCalendarById(@PathVariable Long calendarId) {

        LOGGER.info("Request to get calendar with id {}", calendarId);
        CalendarDetailsDto calendarDetailsDto;
        try {
            calendarDetailsDto = this.calendarServiceImp.getCalendarById(calendarId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get calendar with id {}", calendarId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved calendar with id {}", calendarId);
        return new ResponseEntity<>(calendarDetailsDto, OK);
    }

    /**
     * Get calendar list by event id
     *
     * @param eventId event id
     * @param page    page number
     * @param size    page size
     * @return {@link Paginated<CalendarDetailsDto>} calendar list and Ok httpStatus
     */
    @GetMapping("/events/{eventId}/calendars")
    @Operation(summary = "Get calendars by event id with pagination", description = "Get calendars by event id with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CalendarDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.EVENT_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<Paginated<CalendarDetailsDto>> getCalendarListByEventId(@PathVariable Long eventId,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size) {

        LOGGER.info("Request to get calendar list with event id {}", eventId);
        Paginated<CalendarDetailsDto> calendarList;
        try {
            calendarList = this.calendarServiceImp.getCalendarListByEventId(eventId, page, size);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get calendar list with event id {}", eventId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved calendar list");
        return new ResponseEntity<>(calendarList, OK);
    }

    /**
     * Delete Calendar and tickets associated with itself
     *
     * @param companyId  company id
     * @param eventId    event id
     * @param calendarId calendar id
     */
    @DeleteMapping("/companies/{companyId}/events/{eventId}/calendars/{calendarId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || " +
            "((@authorized.hasRole('COMPANY_ADMIN') || @authorized.hasRole('WORKER')) && @authorized.isOnCompany(#companyId))")
    @Operation(summary = "Delete Calendar",
            description = "Delete Calendar and associated tickets - Access only for users with 'COMPANY_ADMIN' or 'WORKER' roles and the logged in user company id needs to be the same as the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "404", description = ErrorMessages.EVENT_NOT_FOUND + " || " + ErrorMessages.CALENDAR_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<Void> deleteCalendar(@PathVariable Long companyId,
                                               @PathVariable Long eventId,
                                               @PathVariable Long calendarId) {

        LOGGER.info("Request to delete calendar with id - {}", companyId);
        try {
            this.calendarServiceImp.deleteCalendar(companyId, eventId, calendarId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to delete calendar with id {}", calendarId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Calendar with id {} deleted successfully", calendarId);
        return ResponseEntity.noContent().build();
    }
}
