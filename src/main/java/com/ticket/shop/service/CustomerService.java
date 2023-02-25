package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.UpdateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;

/**
 * Common interface for customer services, provides methods to manage customers
 */
public interface CustomerService {

    /**
     * Create new customer
     *
     * @param createCustomerDto {@link CreateCustomerDto}
     * @return {@link CustomerDetailsDto} the customer created
     * @throws UserAlreadyExistsException when the user already exists
     */
    CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto) throws UserAlreadyExistsException;

    /**
     * Get customer by id
     *
     * @param userId user id to be got
     * @return {@link CustomerDetailsDto} the customer obtained
     * @throws UserNotFoundException when the user isn't found
     */
    CustomerDetailsDto getCustomerById(Long userId) throws UserNotFoundException;

    /**
     * Get customers list with pagination
     *
     * @param page page number
     * @param size size of page
     * @return {@link Paginated<CustomerDetailsDto>}
     */
    Paginated<CustomerDetailsDto> getCustomersList(int page, int size);

    /**
     * Update customer
     *
     * @param userId            user id to be updated
     * @param updateCustomerDto {@link UpdateCustomerDto}
     * @return {@link CustomerDetailsDto} the customer updated
     * @throws UserNotFoundException when the user isn't found
     */
    CustomerDetailsDto updateCustomer(Long userId, UpdateCustomerDto updateCustomerDto) throws UserNotFoundException, CompanyNotFoundException;

    /**
     * Delete customer
     *
     * @param customerId customer id
     * @throws UserNotFoundException when the user isn't found
     */
    void deleteCustomer(Long customerId) throws UserNotFoundException;
}
