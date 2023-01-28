package com.ticket.shop.exception.country;

import com.ticket.shop.exception.TicketShopException;

public class CountryNotFoundException extends TicketShopException {
    public CountryNotFoundException(String message) {
        super(message);
    }
}
