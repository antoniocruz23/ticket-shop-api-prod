package com.ticket.shop.command.ticket;

import com.ticket.shop.enumerators.TicketType;
import lombok.Builder;
import lombok.Data;

/**
 * TicketDetailsWhenCreatedDto used to respond with ticket details when created
 */
@Data
@Builder
public class TicketDetailsWhenCreatedDto {
    private TicketType type;
    private Long amountOfTickets;
    private Double price;
}
