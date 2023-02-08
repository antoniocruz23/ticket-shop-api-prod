package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateOrUpdateCompanyDto;
import com.ticket.shop.converter.CompanyConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImp implements CompanyService {

    private static final Logger LOGGER = LogManager.getLogger(CustomerService.class);
    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;
    private final AddressService addressService;

    public CompanyServiceImp(CompanyRepository companyRepository, AddressRepository addressRepository, AddressService addressService) {
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
        this.addressService = addressService;
    }

    /**
     * @see CompanyService#createCompany(CreateOrUpdateCompanyDto)
     */
    @Override
    public CompanyDetailsDto createCompany(CreateOrUpdateCompanyDto createOrUpdateCompanyDto) throws CompanyAlreadyExistsException {

        CompanyEntity companyEntity = CompanyConverter.fromCreateCompanyDtoToCompanyEntity(createOrUpdateCompanyDto);
        validateCompany(companyEntity.getName(), companyEntity.getEmail(), companyEntity.getWebsite());

        AddressDetailsDto address = this.addressService.createAddress(createOrUpdateCompanyDto.getAddress());
        AddressEntity addressEntityById = getAddressEntityById(address.getAddressId());
        companyEntity.setAddressEntity(addressEntityById);

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

    /**
     * Get Address by id
     *
     * @param addressId address id
     * @return {@link CountryEntity}
     */
    private AddressEntity getAddressEntityById(Long addressId) {
        LOGGER.debug("Getting address with id {} from database", addressId);
        return this.addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    LOGGER.error("Address with id {} doesn't exist", addressId);
                    return new AddressNotFoundException(ErrorMessages.ADDRESS_NOT_FOUND);
                });
    }
}
