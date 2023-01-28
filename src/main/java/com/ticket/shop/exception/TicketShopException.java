package com.ticket.shop.exception;

/**
 * Global Ticket Shop exception
 */
public class TicketShopException extends RuntimeException {
    public TicketShopException() {
    }

    public TicketShopException(String message) {
        super(message);
    }

    public TicketShopException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketShopException(Throwable cause) {
        super(cause);
    }

    public TicketShopException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
