package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CalendarDetailsWithTicketsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;

/**
 * Common interface for calendar services, provides methods to manage calendars
 */
public interface CalendarService {

    /**
     * Create new calendar
     *
     * @param createCalendarDto {@link CreateCalendarDto}
     * @param eventId           event id
     * @return {@link CalendarDetailsWithTicketsDto}
     */
    CalendarDetailsWithTicketsDto createCalendar(CreateCalendarDto createCalendarDto, Long eventId);

    /**
     * Get calendar by id
     *
     * @param calendarId calendar id
     * @return {@link CalendarDetailsDto}
     */
    CalendarDetailsDto getCalendarById(Long calendarId);

    /**
     * Get list of calendars by event id
     *
     * @param eventId event id
     * @return {@link Paginated<CalendarDetailsDto>}
     */
    Paginated<CalendarDetailsDto> getCalendarListByEventId(Long eventId, int page, int size);

}
