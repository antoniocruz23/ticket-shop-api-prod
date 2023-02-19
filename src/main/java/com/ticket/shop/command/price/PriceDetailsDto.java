package com.ticket.shop.command.price;

import com.ticket.shop.enumerators.TicketType;
import lombok.Builder;
import lombok.Data;

/**
 * PriceDetailsDto used to respond with prices details
 */
@Data
@Builder
public class PriceDetailsDto {
    private Long priceId;
    private Double price;
    private TicketType type;
}
