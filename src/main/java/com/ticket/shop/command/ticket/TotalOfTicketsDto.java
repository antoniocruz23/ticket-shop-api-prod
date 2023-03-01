package com.ticket.shop.command.ticket;

import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * TotalOfTicketsDto used to respond with ticket total values
 */
@Data
@Builder
public class TotalOfTicketsDto {
    private int totalOfTickets;

    @Schema(example = """
            {"VIP":
                \n{"SOLD": 2
                \n}
            }""")
    private Map<TicketType, Map<TicketStatus, Long>> totalByTypeStatus;
}
