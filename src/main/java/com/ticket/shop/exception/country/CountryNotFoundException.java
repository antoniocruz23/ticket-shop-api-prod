package com.ticket.shop.exception.country;

import com.ticket.shop.exception.TicketShopException;

/**
 * Country not found exception
 */
public class CountryNotFoundException extends TicketShopException {

    public CountryNotFoundException(String message) {
        super(message);
    }
}
