package com.ticket.shop.converter;

import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.persistence.entity.CalendarEntity;

/**
 * Calendar converter
 */
public class CalendarConverter {

    /**
     * From {@link CreateCalendarDto} to {@link CalendarEntity}
     * @param createCalendarDto {@link CreateCalendarDto}
     * @return {@link CalendarEntity}
     */
    public static CalendarEntity fromCreateCalendarDtoToCalendarEntity(CreateCalendarDto createCalendarDto) {
        return CalendarEntity.builder()
                .startDate(createCalendarDto.getStartDate())
                .endDate(createCalendarDto.getEndDate())
                .build();
    }

    /**
     * From {@link CalendarEntity} to {@link CalendarDetailsDto}
     * @param calendarEntity {@link CalendarEntity}
     * @return {@link CalendarDetailsDto}
     */
    public static CalendarDetailsDto fromCalendarEntityToCalendarDetailsDto(CalendarEntity calendarEntity) {
        return CalendarDetailsDto.builder()
                .calendarId(calendarEntity.getCalendarId())
                .startDate(calendarEntity.getStartDate())
                .endDate(calendarEntity.getEndDate())
                .build();
    }
}
