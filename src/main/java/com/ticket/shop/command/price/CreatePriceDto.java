package com.ticket.shop.command.price;

import com.ticket.shop.enumerators.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * CreatePriceDto used to store price info when created
 */
@Data
@Builder
public class CreatePriceDto {

    @Schema(example = "29.99")
    @NotNull(message = "Must have a price")
    private Double price;

    @Schema(example = "VIP")
    @NotNull(message = "Must have a ticket type")
    private TicketType type;
}
