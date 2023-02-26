package com.ticket.shop.converter;

import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CalendarDetailsWithTicketsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.EventEntity;

/**
 * Calendar converter
 */
public class CalendarConverter {

    /**
     * From {@link CreateCalendarDto} to {@link CalendarEntity}
     *
     * @param createCalendarDto {@link CreateCalendarDto}
     * @param eventEntity       {@link EventEntity}
     * @return {@link CalendarEntity}
     */
    public static CalendarEntity fromCreateCalendarDtoToCalendarEntity(CreateCalendarDto createCalendarDto, EventEntity eventEntity) {
        return CalendarEntity.builder()
                .startDate(createCalendarDto.getStartDate())
                .endDate(createCalendarDto.getEndDate())
                .eventEntity(eventEntity)
                .build();
    }

    /**
     * From {@link CalendarEntity} to {@link CalendarDetailsWithTicketsDto}
     *
     * @param calendarEntity {@link CalendarEntity}
     * @return {@link CalendarDetailsWithTicketsDto}
     */
    public static CalendarDetailsWithTicketsDto fromCalendarEntityToCalendarDetailsWithTicketsDto(CalendarEntity calendarEntity) {
        return CalendarDetailsWithTicketsDto.builder()
                .calendarId(calendarEntity.getCalendarId())
                .eventId(calendarEntity.getEventEntity().getEventId())
                .startDate(calendarEntity.getStartDate())
                .endDate(calendarEntity.getEndDate())
                .build();
    }

    /**
     * From {@link CalendarEntity} to {@link CalendarDetailsDto}
     *
     * @param calendarEntity {@link CalendarEntity}
     * @return {@link CalendarDetailsDto}
     */
    public static CalendarDetailsDto fromCalendarEntityToCalendarDetailsDto(CalendarEntity calendarEntity) {
        return CalendarDetailsDto.builder()
                .calendarId(calendarEntity.getCalendarId())
                .eventId(calendarEntity.getEventEntity().getEventId())
                .startDate(calendarEntity.getStartDate())
                .endDate(calendarEntity.getEndDate())
                .build();
    }
}
