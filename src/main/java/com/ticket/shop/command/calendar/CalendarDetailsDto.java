package com.ticket.shop.command.calendar;

import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.time.LocalTime;

/**
 * CalendarDetailsDto used to respond with calendar details
 */
@Data
@Builder
public class CalendarDetailsDto {
    private Long calendarId;
    private DateTime date;
    private LocalTime startTime;
    private LocalTime endTime;
}
