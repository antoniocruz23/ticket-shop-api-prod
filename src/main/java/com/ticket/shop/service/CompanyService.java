package com.ticket.shop.service;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateAndUpdateCompanyDto;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;
import com.ticket.shop.exception.company.CompanyNotFoundException;

/**
 * Common interface for company services, provides methods to manage companies
 */
public interface CompanyService {

    /**
     * Create new company
     *
     * @param createAndUpdateCompanyDto {@link CreateAndUpdateCompanyDto}
     * @return {@link CompanyDetailsDto}
     * @throws CompanyAlreadyExistsException when the company already exists
     */
    CompanyDetailsDto createCompany(CreateAndUpdateCompanyDto createAndUpdateCompanyDto) throws CompanyAlreadyExistsException;

    /**
     * Get company by id
     *
     * @param companyId id
     * @return {@link CreateAndUpdateCompanyDto}
     * @throws CompanyNotFoundException when the company is not found
     */
    CompanyDetailsDto getCompanyById(Long companyId) throws CompanyNotFoundException;

    /**
     * Update company
     *
     * @param companyId company id to be updated
     * @param updateWorkerDto {@link CreateAndUpdateCompanyDto}
     * @return {@link CompanyDetailsDto} the company updated
     * @throws CompanyNotFoundException when the company isn't found
     */
    CompanyDetailsDto updateCompany(Long companyId, CreateAndUpdateCompanyDto updateWorkerDto) throws CompanyNotFoundException;
}
