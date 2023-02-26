package com.ticket.shop.service;

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
     * @return {@link CalendarDetailsWithTicketsDto}
     */
    CalendarDetailsWithTicketsDto createCalendar(CreateCalendarDto createCalendarDto);

    /**
     * Get calendar by id
     *
     * @param calendarId calendar id
     * @return {@link CalendarDetailsDto}
     */
    CalendarDetailsDto getCalendarById(Long calendarId);

}
