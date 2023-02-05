package com.ticket.shop.service;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateOrUpdateCompanyDto;
import com.ticket.shop.converter.CompanyConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.repository.CompanyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImp implements CompanyService {

    private static final Logger LOGGER = LogManager.getLogger(CustomerService.class);
    private final CompanyRepository companyRepository;

    public CompanyServiceImp(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * @see CompanyService#createCompany(CreateOrUpdateCompanyDto)
     */
    @Override
    public CompanyDetailsDto createCompany(CreateOrUpdateCompanyDto createOrUpdateCompanyDto) throws CompanyAlreadyExistsException {

        CompanyEntity companyEntity = CompanyConverter.fromCreateCompanyDtoToCompanyEntity(createOrUpdateCompanyDto);
        validateCompany(companyEntity.getName(), companyEntity.getEmail(), companyEntity.getWebsite());

        LOGGER.info("Persisting company into database");
        CompanyEntity createCompany;
        try {
            LOGGER.info("Saving company on database");
            createCompany = this.companyRepository.save(companyEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving worker into database {}", createOrUpdateCompanyDto, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created company");
        return CompanyConverter.fromCompanyEntityToCompanyDetailsDto(createCompany);
    }

    /**
     * @see CompanyService#getCompanyById(Long)
     */
    @Override
    public CompanyDetailsDto getCompanyById(Long companyId) throws CompanyNotFoundException {
        CompanyEntity companyEntity = getCompanyEntityById(companyId);
        return CompanyConverter.fromCompanyEntityToCompanyDetailsDto(companyEntity);
    }

    /**
     * @see CompanyService#updateCompany(Long, CreateOrUpdateCompanyDto)
     */
    @Override
    public CompanyDetailsDto updateCompany(Long companyId, CreateOrUpdateCompanyDto updateWorkerDto) throws CompanyNotFoundException {
        CompanyEntity companyEntity = getCompanyEntityById(companyId);

        companyEntity.setName(updateWorkerDto.getName());
        companyEntity.setEmail(updateWorkerDto.getEmail());
        companyEntity.setWebsite(updateWorkerDto.getWebsite());

        LOGGER.debug("Updating company with id {} with new data", companyId);
        try {
            this.companyRepository.save(companyEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while updating company with id {} with new data - {}", companyId, companyEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return CompanyConverter.fromCompanyEntityToCompanyDetailsDto(companyEntity);
    }

    private void validateCompany(String name, String email, String website) {
        if (this.companyRepository.findByName(name).isPresent()) {

            LOGGER.error("Duplicated name - {}", name);
            throw new CompanyAlreadyExistsException(ErrorMessages.NAME_ALREADY_EXISTS);
        }

        if (this.companyRepository.findByEmail(email).isPresent()) {

            LOGGER.error("Duplicated email - {}", email);
            throw new CompanyAlreadyExistsException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        if (this.companyRepository.findByWebsite(website).isPresent()) {

            LOGGER.error("Duplicated website - {}", website);
            throw new CompanyAlreadyExistsException(ErrorMessages.WEBSITE_ALREADY_EXISTS);
        }
    }

    /**
     * Get Company by id
     *
     * @param companyId company id
     * @return {@link CompanyEntity}
     */
    private CompanyEntity getCompanyEntityById(Long companyId) {
        LOGGER.debug("Getting company with id {} from database", companyId);
        return this.companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    LOGGER.error("The company with id {} does not exist in database", companyId);
                    return new CompanyNotFoundException(ErrorMessages.COMPANY_NOT_FOUND);
                });
    }
}
