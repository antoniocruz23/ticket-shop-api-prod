package com.ticket.shop.controller;

import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/companies/{companyId}/events")
    public ResponseEntity<EventDetailsDto> createEvent(@Valid @RequestBody CreateEventDto createEventDto) {

        LOGGER.info("Request to create new event - {}", createEventDto);
        EventDetailsDto eventDetailsDto = null;
        try {

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created event - {}", createEventDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        LOGGER.info("Event created successfully. Retrieving created event with id {}", 1L);//eventDetailsDto.getCompanyId());
        return new ResponseEntity<>(eventDetailsDto, HttpStatus.CREATED);
    }
}

