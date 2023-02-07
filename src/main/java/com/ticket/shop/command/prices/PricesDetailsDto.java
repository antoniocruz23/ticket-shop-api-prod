package com.ticket.shop.command.prices;

import com.ticket.shop.enumerators.PriceTypes;
import lombok.Builder;
import lombok.Data;

/**
 * PricesDetailsDto used to respond with prices details
 */
@Data
@Builder
public class PricesDetailsDto {
    private Long price;
    private PriceTypes type;
}
