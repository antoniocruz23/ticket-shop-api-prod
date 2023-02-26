package com.ticket.shop.command.calendar;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CalendarDetailsDto used to respond with calendar details
 */
@Data
@Builder
public class CalendarDetailsDto {
    private Long calendarId;
    private Long eventId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
