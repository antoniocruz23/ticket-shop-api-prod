package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CalendarDetailsWithTicketsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.command.calendar.UpdateCalendarDto;

/**
 * Common interface for calendar services, provides methods to manage calendars
 */
public interface CalendarService {

    /**
     * Create new calendar
     *
     * @param createCalendarDto {@link CreateCalendarDto}
     * @param companyId         company id
     * @param eventId           event id
     * @return {@link CalendarDetailsWithTicketsDto}
     */
    CalendarDetailsWithTicketsDto createCalendar(CreateCalendarDto createCalendarDto, Long companyId, Long eventId);

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

    /**
     * Delete Calendar by id
     *
     * @param companyId  company id
     * @param eventId    event id
     * @param calendarId calendar id
     */
    void deleteCalendar(Long companyId, Long eventId, Long calendarId);

    /**
     * Update calendar by id
     *
     * @param companyId         company id
     * @param calendarId        calendar id
     * @param updateCalendarDto {@link UpdateCalendarDto}
     * @return {@link CalendarDetailsDto}
     */
    CalendarDetailsDto updateCalendar(Long companyId, Long calendarId, UpdateCalendarDto updateCalendarDto);
}
