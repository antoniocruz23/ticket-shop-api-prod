package com.ticket.shop.command.ticketPrice;

import com.ticket.shop.enumerators.PriceTypes;
import lombok.Builder;
import lombok.Data;

/**
 * TicketPriceDetailsDto used to respond with prices details
 */
@Data
@Builder
public class TicketPriceDetailsDto {
    private Long ticketPriceId;
    private Double value;
    private PriceTypes type;
}
