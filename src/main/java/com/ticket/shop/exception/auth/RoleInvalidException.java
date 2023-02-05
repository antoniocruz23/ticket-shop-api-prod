package com.ticket.shop.exception.auth;

import com.ticket.shop.exception.TicketShopException;

/**
 * Role Invalid Exception
 */
public class RoleInvalidException extends TicketShopException {

    public RoleInvalidException(String message) {
        super(message);
    }
}
