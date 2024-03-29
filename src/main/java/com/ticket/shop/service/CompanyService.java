package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateOrUpdateCompanyDto;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;
import com.ticket.shop.exception.company.CompanyNotFoundException;

/**
 * Common interface for company services, provides methods to manage companies
 */
public interface CompanyService {

    /**
     * Create new company
     *
     * @param createOrUpdateCompanyDto {@link CreateOrUpdateCompanyDto}
     * @return {@link CompanyDetailsDto}
     * @throws CompanyAlreadyExistsException when the company already exists
     */
    CompanyDetailsDto createCompany(CreateOrUpdateCompanyDto createOrUpdateCompanyDto);

    /**
     * Get company by id
     *
     * @param companyId id
     * @return {@link CreateOrUpdateCompanyDto}
     * @throws CompanyNotFoundException when the company is not found
     */
    CompanyDetailsDto getCompanyById(Long companyId);

    /**
     * Update company
     *
     * @param companyId       company id to be updated
     * @param updateWorkerDto {@link CreateOrUpdateCompanyDto}
     * @return {@link CompanyDetailsDto} the company updated
     * @throws CompanyNotFoundException when the company isn't found
     */
    CompanyDetailsDto updateCompany(Long companyId, CreateOrUpdateCompanyDto updateWorkerDto);

    /**
     * Delete company
     *
     * @param companyId company id
     */
    void deleteCompany(Long companyId);

    /**
     * Get companies with Pagination
     *
     * @param page page
     * @param size size
     * @return {@link Paginated<CompanyDetailsDto>}
     */
    Paginated<CompanyDetailsDto> getCompanyList(int page, int size);
}
