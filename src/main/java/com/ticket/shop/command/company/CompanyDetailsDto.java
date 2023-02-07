package com.ticket.shop.command.company;

import com.ticket.shop.command.address.AddressDetailsDto;
import lombok.Builder;
import lombok.Data;

/**
 * CompanyDetailsDto used to respond with company details
 */
@Data
@Builder
public class CompanyDetailsDto {
    private Long companyId;
    private String name;
    private String email;
    private String website;
    private AddressDetailsDto address;
}
