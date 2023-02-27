package com.ticket.shop.service;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsWhenCreatedDto;

import java.util.List;

/**
 * Common interface for ticket services, provides methods to manage tickets
 */
public interface TicketService {

    /**
     * Bulk creation of tickets
     *
     * @param companyId           company id
     * @param calendarId          calendar id
     * @param createTicketDtoList {@link List<CreateTicketDto>}
     * @return {@link List<TicketDetailsWhenCreatedDto>}
     */
    List<TicketDetailsWhenCreatedDto> bulkCreateTicket(Long companyId, Long calendarId, List<CreateTicketDto> createTicketDtoList);

    /**
     * Delete tickets by company id and calendar id
     *
     * @param companyId  company id
     * @param calendarId calendar id
     */
    void deleteTicketsByCalendarId(Long companyId, Long calendarId);
}
