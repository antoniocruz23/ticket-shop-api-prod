package com.ticket.shop.command.country;

import lombok.Builder;
import lombok.Data;

/**
 * CountryDetailsDto used to respond with country details
 */
@Data
@Builder
public class CountryDetailsDto {
    private Long countryId;
    private String name;
    private String isoCode2;
    private String isoCode3;
    private String phoneCode;
    private String currency;
    private String language;
}