package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.converter.AddressConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImp implements AddressService {

    private static final Logger LOGGER = LogManager.getLogger(AddressService.class);
    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    public AddressServiceImp(AddressRepository addressRepository, CountryRepository countryRepository) {
        this.addressRepository = addressRepository;
        this.countryRepository = countryRepository;
    }

    /**
     * @see AddressService#createAddress(CreateAddressDto)
     */
    @Override
    public AddressDetailsDto createAddress(CreateAddressDto createAddressDto) {
        CountryEntity countryEntity = getCountryEntityById(createAddressDto.getCountryId());
        AddressEntity addressEntity = AddressConverter.fromCreateAddressDtoToAddressEntity(createAddressDto);
        addressEntity.setCountryEntity(countryEntity);

        LOGGER.info("Persisting address into database");
        AddressEntity createAddress;
        try {
            LOGGER.info("Saving address on database");
            createAddress = this.addressRepository.save(addressEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving address into database {}", createAddressDto, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return AddressConverter.fromAddressEntityToAddressDetailsDto(createAddress);
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
