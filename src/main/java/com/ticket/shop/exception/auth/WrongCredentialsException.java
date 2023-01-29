package com.ticket.shop.exception.auth;

import com.ticket.shop.exception.TicketShopException;

/**
 * Wrong Credentials Exception
 */
public class WrongCredentialsException extends TicketShopException {

    public WrongCredentialsException(String message) {
        super(message);
    }
}
