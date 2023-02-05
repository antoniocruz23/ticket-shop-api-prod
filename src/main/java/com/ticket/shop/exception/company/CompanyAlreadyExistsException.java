package com.ticket.shop.exception.company;

import com.ticket.shop.exception.TicketShopException;

/**
 * Company already exists exception
 */
public class CompanyAlreadyExistsException extends TicketShopException {

    public CompanyAlreadyExistsException(String message) {
        super(message);
    }
}
