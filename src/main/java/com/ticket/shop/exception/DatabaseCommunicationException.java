package com.ticket.shop.exception;

/**
 * DataBase Communication Exception
 */
public class DatabaseCommunicationException extends TicketShopException {
    public DatabaseCommunicationException(String message, Throwable e) {
        super(message, e);
    }
}