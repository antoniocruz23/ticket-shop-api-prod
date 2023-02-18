package com.ticket.shop.service;


import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.UpdateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.enumerators.UserRole;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.CountryRepository;
import com.ticket.shop.persistence.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserService Implementation
 */
@Service
public class CustomerServiceImp implements CustomerService {

    private static final Logger LOGGER = LogManager.getLogger(CustomerService.class);
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImp(UserRepository userRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @see CustomerService#createCustomer(CreateCustomerDto)
     */
    @Override
    public CustomerDetailsDto createCustomer(CreateCustomerDto createUserDto) throws UserAlreadyExistsException {

        LOGGER.debug("Creating customer - {}", createUserDto);
        UserEntity userEntity = UserConverter.fromCreateCustomerDtoToUserEntity(createUserDto);

        userEntity.setRoles(List.of(UserRole.CUSTOMER));

        CountryEntity countryEntity = getCountryEntityById(createUserDto.getCountryId());
        userEntity.setCountryEntity(countryEntity);

        String encryptedPassword = this.passwordEncoder.encode(createUserDto.getPassword());
        userEntity.setEncryptedPassword(encryptedPassword);

        if (this.userRepository.findByEmail(userEntity.getEmail()).isPresent()) {

            LOGGER.error("Duplicated email - {}", userEntity.getEmail());
            throw new UserAlreadyExistsException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        LOGGER.info("Persisting customer into database");
        UserEntity createdCustomer;
        try {
            LOGGER.info("Saving customer on database");
            createdCustomer = this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving customer into database {}", userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created customer");
        return UserConverter.fromUserEntityToCustomerDetailsDto(createdCustomer);
    }

    /**
     * @see CustomerService#getCustomerById(Long)
     */
    @Override
    public CustomerDetailsDto getCustomerById(Long userId) throws UserNotFoundException {
        return UserConverter.fromUserEntityToCustomerDetailsDto(getUserEntityById(userId));
    }

    /**
     * @see CustomerService#updateCustomer(Long, UpdateCustomerDto)
     */
    @Override
    public CustomerDetailsDto updateCustomer(Long userId, UpdateCustomerDto updateCustomerDto) throws UserNotFoundException {

        UserEntity userEntity = getUserEntityById(userId);
        CountryEntity countryEntity = getCountryEntityById(updateCustomerDto.getCountryId());
        String encryptedPassword = this.passwordEncoder.encode(updateCustomerDto.getPassword());

        userEntity.setFirstname(updateCustomerDto.getFirstname());
        userEntity.setLastname(updateCustomerDto.getLastname());
        userEntity.setEmail(updateCustomerDto.getEmail());
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setCountryEntity(countryEntity);

        LOGGER.debug("Updating customer with id {} with new data", userId);
        try {
            this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while updating customer with id {} with new data - {}", userId, userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return UserConverter.fromUserEntityToCustomerDetailsDto(userEntity);
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
                    LOGGER.error("The user with id {} does not exist in database", userId);
                    return new UserNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });
    }
}
