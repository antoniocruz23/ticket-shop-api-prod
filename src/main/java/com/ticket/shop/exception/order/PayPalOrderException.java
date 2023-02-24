package com.ticket.shop.exception.order;

import com.ticket.shop.exception.TicketShopException;

/**
 * PayPal Order Exception
 */
public class PayPalOrderException extends TicketShopException {

    public PayPalOrderException(String message, Throwable e) {
        super(message, e);
    }
}