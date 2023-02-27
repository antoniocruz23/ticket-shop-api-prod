package com.ticket.shop.exception.auth;

import com.ticket.shop.exception.TicketShopException;

/**
 * Invalid Role Exception
 */
public class InvalidRoleException extends TicketShopException {

    public InvalidRoleException(String message) {
        super(message);
    }
}
