package com.ticket.shop.exception.company;

import com.ticket.shop.exception.TicketShopException;

/**
 * Company not found exception
 */
public class CompanyNotFoundException extends TicketShopException {

    public CompanyNotFoundException(String message) {
        super(message);
    }
}
