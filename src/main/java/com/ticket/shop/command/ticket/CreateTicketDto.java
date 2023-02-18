package com.ticket.shop.command.ticket;

import com.ticket.shop.enumerators.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * CreateTicketDto used to store ticket info when created
 */
@Data
@Builder
public class CreateTicketDto {

    @Schema(example = "VIP")
    @NotNull(message = "Must have a ticket type")
    private TicketType type;

    @Schema(example = "10")
    @NotNull(message = "Must have total number of tickets")
    private Long total;
}
