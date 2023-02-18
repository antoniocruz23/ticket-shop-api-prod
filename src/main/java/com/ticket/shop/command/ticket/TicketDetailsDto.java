package com.ticket.shop.command.ticket;

import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import lombok.Builder;
import lombok.Data;

/**
 * TicketDetailsDto used to respond with ticket details
 */
@Data
@Builder
public class TicketDetailsDto {
    private Long ticketId;
    private TicketStatus status;
    private TicketType type;
    private Double price;
}
