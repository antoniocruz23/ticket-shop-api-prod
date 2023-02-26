package com.ticket.shop.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * @return the response entity
     */
    @PostMapping("/calendars")
    @PreAuthorize("@authorized.hasRole('ADMIN') || " +
            "((@authorized.hasRole('COMPANY_ADMIN') || @authorized.hasRole('WORKER')) && @authorized.isOnCompany(#createCalendarDto.companyId))")
    @Operation(summary = "Registration", description = "Register new calendar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CalendarDetailsWithTicketsDto.class))),
            @ApiResponse(responseCode = "403", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.EVENT_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CalendarDetailsWithTicketsDto> createCalendar(@Valid @RequestBody CreateCalendarDto createCalendarDto) {

        LOGGER.info("Request to create new calendar - {}", createCalendarDto);
        CalendarDetailsWithTicketsDto calendarDetailsDto;
        try {
            calendarDetailsDto = this.calendarServiceImp.createCalendar(createCalendarDto);

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
     * @return {@link CalendarDetailsWithTicketsDto} the calendar wanted and Ok httpStatus
     */
    @GetMapping("/calendars/{calendarId}")
    @Operation(summary = "Get calendar by id", description = "Get calendar by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CalendarDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.CALENDAR_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
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
}
