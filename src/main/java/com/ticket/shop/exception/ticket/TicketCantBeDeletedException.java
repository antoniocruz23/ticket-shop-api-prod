package com.ticket.shop.exception.ticket;

import com.ticket.shop.exception.TicketShopException;

/**
 * Ticket Can't Be Deleted Exception
 */
public class TicketCantBeDeletedException extends TicketShopException {
    public TicketCantBeDeletedException(String message) {
        super(message);
    }
}
