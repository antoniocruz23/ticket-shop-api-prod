package com.ticket.shop.service;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateCompanyDto;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;

/**
 * Common interface for company services, provides methods to manage companies
 */
public interface CompanyService {

    /**
     * Create new company
     *
     * @param createCompanyDto {@link CreateCompanyDto}
     * @return {@link CompanyDetailsDto}
     * @throws CompanyAlreadyExistsException when the company already exists
     */
    CompanyDetailsDto createCompany(CreateCompanyDto createCompanyDto) throws CompanyAlreadyExistsException;
}
