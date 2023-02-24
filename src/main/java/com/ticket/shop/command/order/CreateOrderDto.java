package com.ticket.shop.command.order;

import com.ticket.shop.enumerators.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;


/**
 * CreateOrderDto used to store order info when created
 */
@Data
@Builder
public class CreateOrderDto {

    @Schema(example = "1")
    private Long eventId;

    @Schema(example = "1")
    private Long calendarId;

    @Schema(example = "1")
    private Long customerId;

    @Schema(example = "GENERAL")
    private TicketType ticketType;

    @Schema(example = "1")
    @Min(value = 1, message = "The given number for numberOfTickets must be greater than or equal to 1")
    private Long numberOfTickets;

    @Schema(example = "1")
    @Min(value = 1, message = "The given number for totalAmount must be greater than or equal to 1")
    private Double totalAmount;
}
