package com.ticket.shop.exception.auth;

import com.ticket.shop.exception.TicketShopException;

/**
 * Invalid Reset Password Token Exception
 */
public class InvalidResetPasswordTokenException extends TicketShopException {
    public InvalidResetPasswordTokenException(String message) {
        super(message);
    }
}
