package com.ticket.shop.service;

import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;

/**
 * Common interface for calendar services, provides methods to manage calendars
 */
public interface CalendarService {

    /**
     * Create new calendar
     *
     * @param createCalendarDto {@link CreateCalendarDto}
     * @return {@link CalendarDetailsDto}
     */
    CalendarDetailsDto createCalendar(CreateCalendarDto createCalendarDto, Long companyId, Long eventId);

}
