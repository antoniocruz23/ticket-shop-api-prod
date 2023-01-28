package com.ticket.shop.exception.user;

import com.ticket.shop.exception.TicketShopException;

/**
 * User already exists exception
 */
public class UserAlreadyExistsException extends TicketShopException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
