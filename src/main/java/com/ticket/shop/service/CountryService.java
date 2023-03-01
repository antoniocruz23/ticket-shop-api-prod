package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.country.CountryDetailsDto;
import com.ticket.shop.exception.country.CountryNotFoundException;

/**
 * Common interface for country services, provides methods to manage countries
 */
public interface CountryService {

    /**
     * Get Country by id
     *
     * @param countryId id
     * @return {@link CountryDetailsDto}
     * @throws CountryNotFoundException when the country is not found
     */
    CountryDetailsDto getCountryById(Long countryId);

    /**
     * Get Countries with Pagination
     *
     * @param page page
     * @param size size
     * @return {@link Paginated<CountryDetailsDto>}
     */
    Paginated<CountryDetailsDto> getCountryList(int page, int size);
}
