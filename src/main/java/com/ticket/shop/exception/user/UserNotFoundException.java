package com.ticket.shop.exception.user;

import com.ticket.shop.exception.TicketShopException;

/**
 * User not found exception
 */
public class UserNotFoundException extends TicketShopException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
