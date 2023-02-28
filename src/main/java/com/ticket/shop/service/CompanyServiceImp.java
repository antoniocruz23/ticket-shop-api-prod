package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateOrUpdateCompanyDto;
import com.ticket.shop.converter.CompanyConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link CompanyService} implementation
 */
@Service
public class CompanyServiceImp implements CompanyService {

    private static final Logger LOGGER = LogManager.getLogger(CompanyService.class);
    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;
    private final AddressServiceImp addressServiceImp;
    private final CountryRepository countryRepository;

    public CompanyServiceImp(CompanyRepository companyRepository, AddressRepository addressRepository, AddressServiceImp addressService, CountryRepository countryRepository) {
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
        this.addressServiceImp = addressService;
        this.countryRepository = countryRepository;
    }

    /**
     * @see CompanyService#createCompany(CreateOrUpdateCompanyDto)
     */
    @Override
    public CompanyDetailsDto createCompany(CreateOrUpdateCompanyDto createOrUpdateCompanyDto) {

        CompanyEntity companyEntity = CompanyConverter.fromCreateCompanyDtoToCompanyEntity(createOrUpdateCompanyDto);
        validateCompany(companyEntity.getName(), companyEntity.getEmail(), companyEntity.getWebsite());

        AddressDetailsDto address = this.addressServiceImp.createAddress(createOrUpdateCompanyDto.getAddress());
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
    public CompanyDetailsDto updateCompany(Long companyId, CreateOrUpdateCompanyDto updateWorkerDto) {
        CompanyEntity companyEntity = getCompanyEntityById(companyId);
        companyEntity.setName(updateWorkerDto.getName());
        companyEntity.setEmail(updateWorkerDto.getEmail());
        companyEntity.setWebsite(updateWorkerDto.getWebsite());

        updateAddress(companyEntity.getAddressEntity(), updateWorkerDto.getAddress());

        LOGGER.debug("Updating company with id {} with new data", companyId);
        try {
            this.companyRepository.save(companyEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while updating company with id {} with new data - {}", companyId, companyEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return CompanyConverter.fromCompanyEntityToCompanyDetailsDto(companyEntity);
    }

    /**
     * @see CompanyService#deleteCompany(Long)
     */
    @Override
    public void deleteCompany(Long companyId) {
        LOGGER.debug("Getting company with id {} from database", companyId);
        CompanyEntity companyEntity = getCompanyEntityById(companyId);

        LOGGER.debug("Deleting company with id {} from database", companyId);
        try {
            this.companyRepository.delete(companyEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while deleting company with id {} from database", companyId, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }
    }

    /**
     * @see CompanyService#getCompanyList(int, int)
     */
    @Override
    public Paginated<CompanyDetailsDto> getCompanyList(int page, int size) {
        LOGGER.debug("Getting companies page {} from database", page);
        Page<CompanyEntity> companyList;
        try {
            companyList = this.companyRepository.findAll(PageRequest.of(page, size));

        } catch (Exception e) {
            LOGGER.error("Failed at getting companies page {} from database", page, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Converting company list to CompanyDetailsDto");
        List<CompanyDetailsDto> companyListResponse = new ArrayList<>();
        for (CompanyEntity companyEntity : companyList) {
            companyListResponse.add(CompanyConverter.fromCompanyEntityToCompanyDetailsDto(companyEntity));
        }

        return new Paginated<>(
                companyListResponse,
                page,
                companyListResponse.size(),
                companyList.getTotalPages(),
                companyList.getTotalElements());
    }

    /**
     * Update Address
     *
     * @param addressEntity     address
     * @param addressDetailsDto new address details
     */
    private void updateAddress(AddressEntity addressEntity, CreateAddressDto addressDetailsDto) {
        addressEntity.setLine1(addressDetailsDto.getLine1());
        addressEntity.setLine2(addressDetailsDto.getLine2());
        addressEntity.setLine3(addressDetailsDto.getLine3());
        addressEntity.setMobileNumber(addressDetailsDto.getMobileNumber());
        addressEntity.setPostCode(addressDetailsDto.getPostCode());
        addressEntity.setCity(addressDetailsDto.getCity());
        CountryEntity countryEntity = getCountryEntityById(addressDetailsDto.getCountryId());
        addressEntity.setCountryEntity(countryEntity);
    }

    /**
     * Validate Company by name, email and website
     *
     * @param name    name
     * @param email   email
     * @param website website
     */
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

    /**
     * Get Country by id
     *
     * @param countryId country id
     * @return {@link CountryEntity}
     */
    private CountryEntity getCountryEntityById(Long countryId) {
        LOGGER.debug("Getting country with id {} from database", countryId);
        return this.countryRepository.findById(countryId)
                .orElseThrow(() -> {
                    LOGGER.error("Country with id {} doesn't exist", countryId);
                    return new CountryNotFoundException(ErrorMessages.COUNTRY_NOT_FOUND);
                });
    }
}
