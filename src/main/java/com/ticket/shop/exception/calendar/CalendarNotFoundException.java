package com.ticket.shop.exception.calendar;

import com.ticket.shop.exception.TicketShopException;

/**
 * Calendar not found exception
 */
public class CalendarNotFoundException extends TicketShopException {

    public CalendarNotFoundException(String message) {
        super(message);
    }

}
