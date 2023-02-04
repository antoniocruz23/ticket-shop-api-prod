package com.ticket.shop.command.customer;

import lombok.Builder;
import lombok.Data;

/**
 * CustomerDetailsDto used to respond with customer details
 */
@Data
@Builder
public class CustomerDetailsDto {
    private Long userId;
    private String firstname;
    private String lastname;
    private String email;
    private Long countryId;
}
