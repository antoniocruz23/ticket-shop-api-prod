package com.ticket.shop.service;

import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;

/**
 * Common interface for event services, provides methods to manage events
 */
public interface EventService {

    /**
     * Create new event
     *
     * @param createEventDto {@link CreateEventDto}
     * @return {@link EventDetailsDto} the event created
     */
    EventDetailsDto createEvent(CreateEventDto createEventDto, Long companyId);
}
