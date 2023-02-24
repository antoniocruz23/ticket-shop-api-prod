package com.ticket.shop.exception.order;

import com.ticket.shop.exception.TicketShopException;

/**
 * Order Capture Exception
 */
public class OrderCaptureException extends TicketShopException {

    public OrderCaptureException(String message, Throwable e) {
        super(message, e);
    }
}
