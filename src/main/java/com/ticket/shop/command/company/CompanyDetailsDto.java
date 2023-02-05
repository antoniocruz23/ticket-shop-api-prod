package com.ticket.shop.command.company;

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
}
