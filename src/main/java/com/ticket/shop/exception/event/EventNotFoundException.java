package com.ticket.shop.exception.event;

import com.ticket.shop.exception.TicketShopException;

/**
 * Event not found exception
 */
public class EventNotFoundException extends TicketShopException {

    public EventNotFoundException(String message) {
        super(message);
    }

}
