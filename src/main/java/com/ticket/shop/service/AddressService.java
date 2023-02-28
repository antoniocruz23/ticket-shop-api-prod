package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;

/**
 * Common interface for address services, provides methods to manage addresses
 */
public interface AddressService {

    /**
     * Create new address
     *
     * @param createAddressDto {@link CreateAddressDto}
     * @return {@link AddressDetailsDto}
     */
    AddressDetailsDto createAddress(CreateAddressDto createAddressDto);

    /**
     * Create Address for a user (Customer or worker)
     *
     * @param userId           user id
     * @param createAddressDto {@link CreateAddressDto}
     * @return {@link AddressDetailsDto}
     */
    AddressDetailsDto createUserAddress(Long userId, CreateAddressDto createAddressDto);

}
