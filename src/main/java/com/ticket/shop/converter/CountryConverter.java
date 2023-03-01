package com.ticket.shop.converter;

import com.ticket.shop.command.country.CountryDetailsDto;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;

/**
 * Country converter
 */
public class CountryConverter {

    /**
     * From {@link CountryEntity} to {@link CountryDetailsDto}
     *
     * @param countryEntity {@link CompanyEntity}
     * @return {@link CountryDetailsDto}
     */
    public static CountryDetailsDto fromCountryEntityToCountryDetailsDto(CountryEntity countryEntity) {
        return CountryDetailsDto.builder()
                .countryId(countryEntity.getCountryId())
                .name(countryEntity.getName())
                .isoCode2(countryEntity.getIsoCode2())
                .isoCode3(countryEntity.getIsoCode3())
                .phoneCode(countryEntity.getPhoneCode())
                .currency(countryEntity.getCurrency())
                .language(countryEntity.getLanguage())
                .build();
    }
}