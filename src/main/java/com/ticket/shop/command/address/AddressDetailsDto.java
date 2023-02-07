package com.ticket.shop.command.address;

import lombok.Builder;
import lombok.Data;

/**
 * AddressDetailsDto used to respond with address details
 */
@Data
@Builder
public class AddressDetailsDto {
    private Long addressId;
    private String line1;
    private String line2;
    private String line3;
    private String mobileNumber;
    private String postCode;
    private Long country;
}
