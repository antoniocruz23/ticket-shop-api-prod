package com.ticket.shop.exception.ticket;

import com.ticket.shop.exception.TicketShopException;

/**
 * Invalid Ticket Type Exception
 */
public class InvalidTicketTypeException extends TicketShopException {
    public InvalidTicketTypeException(String message) {
        super(message);
    }
}
