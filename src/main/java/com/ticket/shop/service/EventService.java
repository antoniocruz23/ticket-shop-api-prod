package com.ticket.shop.service;

import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.command.event.EventDetailsWithCalendarIdsDto;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;

/**
 * Common interface for event services, provides methods to manage events
 */
public interface EventService {

    /**
     * Create new event
     *
     * @param createEventDto {@link CreateEventDto}
     * @return {@link EventDetailsDto} the event created
     * @throws AddressNotFoundException when the address isn't found
     * @throws CompanyNotFoundException when the company isn't found
     * @throws CountryNotFoundException when the country isn't found
     */
    EventDetailsDto createEvent(CreateEventDto createEventDto, Long companyId);

    /**
     * Get event by id
     * @param eventId event id
     * @return {@link EventDetailsWithCalendarIdsDto}
     * @throws EventNotFoundException when the event isn't found
     */
    EventDetailsWithCalendarIdsDto getEventById(Long eventId);
}
