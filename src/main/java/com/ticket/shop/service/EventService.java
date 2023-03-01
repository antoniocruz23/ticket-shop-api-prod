package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.command.event.EventDetailsWithCalendarIdsDto;
import com.ticket.shop.command.event.UpdateEventDto;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;

import java.util.Date;

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
     *
     * @param eventId event id
     * @return {@link EventDetailsWithCalendarIdsDto}
     * @throws EventNotFoundException when the event isn't found
     */
    EventDetailsWithCalendarIdsDto getEventById(Long eventId);

    /**
     * Get event list with pagination
     *
     * @param page      page
     * @param size      page size
     * @param companyId company id
     * @param date      event date
     * @return {@link Paginated<EventDetailsDto>}
     */
    Paginated<EventDetailsDto> getEventList(int page, int size, Long companyId, Date date);

    /**
     * Update event by id
     *
     * @param companyId      company id
     * @param eventId        event id
     * @param updateEventDto {@link UpdateEventDto}
     * @return {@link EventDetailsDto}
     */
    EventDetailsDto updateEvent(Long companyId, Long eventId, UpdateEventDto updateEventDto);

    /**
     * Delete event
     *
     * @param companyId company id
     * @param eventId   event id
     */
    void deleteEvent(Long companyId, Long eventId);
}
