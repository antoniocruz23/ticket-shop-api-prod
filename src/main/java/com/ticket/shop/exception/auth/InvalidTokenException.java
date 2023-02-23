package com.ticket.shop.exception.auth;

import com.ticket.shop.exception.TicketShopException;

/**
 * Invalid Token Exception
 */
public class InvalidTokenException extends TicketShopException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
