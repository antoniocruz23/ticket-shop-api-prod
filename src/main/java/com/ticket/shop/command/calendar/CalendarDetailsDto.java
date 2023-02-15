package com.ticket.shop.command.calendar;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

/**
 * CalendarDetailsDto used to respond with calendar details
 */
@Data
@Builder
public class CalendarDetailsDto {
    private Long calendarId;
    private Date date;
    private LocalTime startTime;
    private LocalTime endTime;
}
