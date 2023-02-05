package com.ticket.shop.service;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateCompanyDto;
import com.ticket.shop.converter.CompanyConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;
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

    @Override
    public CompanyDetailsDto createCompany(CreateCompanyDto createCompanyDto) throws CompanyAlreadyExistsException {

        CompanyEntity companyEntity = CompanyConverter.fromCreateCompanyDtoToCompanyEntity(createCompanyDto);

        validateCompany(companyEntity.getName(), companyEntity.getEmail(), companyEntity.getWebsite());

        LOGGER.info("Persisting company into database");
        CompanyEntity createCompany;
        try {
            LOGGER.info("Saving company on database");
            createCompany = this.companyRepository.save(companyEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving worker into database {}", createCompanyDto, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created company");
        return CompanyConverter.fromCompanyEntityToCompanyDetailsDto(createCompany);
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
}
