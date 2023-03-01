package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.converter.AddressConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
import com.ticket.shop.persistence.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * An {@link AddressService} implementation
 */
@Service
public class AddressServiceImp implements AddressService {

    private static final Logger LOGGER = LogManager.getLogger(AddressService.class);
    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;
    private final UserRepository userRepository;

    public AddressServiceImp(AddressRepository addressRepository, CountryRepository countryRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.countryRepository = countryRepository;
        this.userRepository = userRepository;
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
     * @see AddressService#createUserAddress(Long, CreateAddressDto)
     */
    @Override
    public AddressDetailsDto createUserAddress(Long userId, CreateAddressDto createAddressDto) {
        CountryEntity countryEntity = getCountryEntityById(createAddressDto.getCountryId());
        UserEntity userEntity = getUserEntityById(userId);

        AddressEntity addressEntity = AddressConverter.fromCreateAddressDtoToAddressEntity(createAddressDto);
        addressEntity.setCountryEntity(countryEntity);
        addressEntity.setUserEntity(userEntity);

        LOGGER.info("Persisting address into database");
        AddressEntity createdAddress;
        try {
            LOGGER.info("Saving address on database");
            createdAddress = this.addressRepository.save(addressEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving address into database {} for user id {}", createAddressDto, userId, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return AddressConverter.fromAddressEntityToAddressDetailsDto(createdAddress);
    }

    /**
     * Update Address
     *
     * @param addressEntity     address
     * @param addressDetailsDto new address details
     */
    protected void updateAddress(AddressEntity addressEntity, CreateAddressDto addressDetailsDto) {
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

    /**
     * Get User by id
     *
     * @param userId user id
     * @return {@link UserEntity}
     */
    private UserEntity getUserEntityById(Long userId) {
        LOGGER.debug("Getting user with id {} from database", userId);
        return this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} doesn't exist", userId);
                    return new UserNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });
    }
}
