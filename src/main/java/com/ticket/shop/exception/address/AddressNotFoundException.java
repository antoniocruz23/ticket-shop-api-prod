package com.ticket.shop.exception.address;

import com.ticket.shop.exception.TicketShopException;

/**
 * Address not found exception
 */
public class AddressNotFoundException extends TicketShopException {

    public AddressNotFoundException(String message) {
        super(message);
    }
}
