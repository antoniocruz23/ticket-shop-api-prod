package com.ticket.shop.command.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * EventDetailsWithCalendarIdsDto used to respond with event details with calendar ids
 */
@Data
@Builder
public class EventDetailsWithCalendarIdsDto {
    private EventDetailsDto eventDetailsDto;
    private List<Long> calendarIds;
}
