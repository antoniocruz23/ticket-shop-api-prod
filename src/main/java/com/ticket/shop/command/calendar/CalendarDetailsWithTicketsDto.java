package com.ticket.shop.command.calendar;

import com.ticket.shop.command.ticket.TicketDetailsWhenCreatedDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CalendarDetailsDto used to respond with calendar details and ticket type and amount
 */
@Data
@Builder
public class CalendarDetailsWithTicketsDto {
    private Long calendarId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<TicketDetailsWhenCreatedDto> tickets;
}
