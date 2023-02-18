package com.ticket.shop.service;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsDto;
import com.ticket.shop.persistence.entity.CalendarEntity;

import java.util.List;

/**
 * Common interface for ticket services, provides methods to manage tickets
 */
public interface TicketService {

    /**
     * Bulk creation of tickets
     *
     * @param createTicketDtoList {@link List<CreateTicketDto>}
     * @param calendar {@link CalendarEntity}
     * @return {@link List<TicketDetailsDto>}
     */
    List<TicketDetailsDto> bulkCreateTicket(List<CreateTicketDto> createTicketDtoList, CalendarEntity calendar);
}
