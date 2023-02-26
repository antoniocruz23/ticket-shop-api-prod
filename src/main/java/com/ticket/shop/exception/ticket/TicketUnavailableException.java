package com.ticket.shop.exception.ticket;

import com.ticket.shop.exception.TicketShopException;

/**
 * Ticket Unavailable Exception
 */
public class TicketUnavailableException extends TicketShopException {
    public TicketUnavailableException(String message) {
        super(message);
    }
}
