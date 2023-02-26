package com.ticket.shop.command.calendar;

import com.ticket.shop.enumerators.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * CalendarDetailsDto used to respond with calendar details and ticket type and amount
 */
@Data
@Builder
public class CalendarDetailsWithTicketsDto {
    private Long calendarId;
    private Long eventId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Schema(example = "{\n\"VIP\":\n 10\n}")
    private Map<TicketType, Long> tickets;
}
