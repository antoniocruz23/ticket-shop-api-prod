package com.ticket.shop.service;


import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.command.customer.UpdateCustomerDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * An {@link CustomerService} implementation
 */
@Service
public class CustomerServiceImp implements CustomerService {

    private static final Logger LOGGER = LogManager.getLogger(CustomerService.class);
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthServiceImp authServiceImp;
    private final EmailServiceImp emailServiceImp;

    @Value("${ticket-shop.resetPassToken.expiresInHours}")
    private long expiresInHours;

    public CustomerServiceImp(UserRepository userRepository, CountryRepository countryRepository,
                              PasswordEncoder passwordEncoder, AuthServiceImp authServiceImp, EmailServiceImp emailServiceImp) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
        this.authServiceImp = authServiceImp;
        this.emailServiceImp = emailServiceImp;
    }

    /**
     * @see CustomerService#createCustomer(CreateCustomerDto)
     */
    @Override
    public CustomerDetailsDto createCustomer(CreateCustomerDto createUserDto) {
        if (this.userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            LOGGER.error("Duplicated email - {}", createUserDto.getEmail());
            throw new UserAlreadyExistsException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        UserEntity userEntity = buildUserEntity(createUserDto);

        LOGGER.info("Persisting customer into database");
        UserEntity createdCustomer;
        try {
            LOGGER.info("Saving customer on database");
            createdCustomer = this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving customer into database {}", userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        this.emailServiceImp.sendEmailToConfirmEmailAddress(
                createdCustomer.getFirstname(),
                createdCustomer.getEmail(),
                createdCustomer.getConfirmEmailToken(),
                this.expiresInHours
        );

        LOGGER.debug("Retrieving created customer");
        return UserConverter.fromUserEntityToCustomerDetailsDto(createdCustomer);
    }

    private UserEntity buildUserEntity(CreateCustomerDto createCustomerDto) {
        LOGGER.debug("Creating customer - {}", createCustomerDto);
        UserEntity userEntity = UserConverter.fromCreateCustomerDtoToUserEntity(createCustomerDto);
        userEntity.setRoles(Set.of(UserRole.CUSTOMER));

        CountryEntity countryEntity = getCountryEntityById(createCustomerDto.getCountryId());
        userEntity.setCountryEntity(countryEntity);

        String encryptedPassword = this.passwordEncoder.encode(createCustomerDto.getPassword());
        userEntity.setEncryptedPassword(encryptedPassword);

        // Insert data about email validation
        String emailToken = this.authServiceImp.generateTokenForValidations();
        Date expiresAt = new Date(new Date().getTime() + Duration.ofHours(2).toMillis());
        userEntity.setConfirmEmailToken(emailToken);
        userEntity.setConfirmEmailExpireToken(expiresAt);
        userEntity.setEmailConfirmed(false);

        return userEntity;
    }

    /**
     * @see CustomerService#getCustomerById(Long)
     */
    @Override
    public CustomerDetailsDto getCustomerById(Long userId) {
        return UserConverter.fromUserEntityToCustomerDetailsDto(getUserEntityById(userId));
    }

    /**
     * @see CustomerService#getCustomersList(int, int)
     */
    @Override
    public Paginated<CustomerDetailsDto> getCustomersList(int page, int size) {
        LOGGER.debug("Getting all customers from database");
        Page<UserEntity> customersList;
        try {
            customersList = this.userRepository.findByRolesContains(UserRole.CUSTOMER, PageRequest.of(page, size));

        } catch (Exception e) {
            LOGGER.error("Failed at getting customers page from database", e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Converting customers list to CustomerDetailsDto");
        List<CustomerDetailsDto> customerListResponse = new ArrayList<>();
        for (UserEntity customer : customersList) {
            customerListResponse.add(UserConverter.fromUserEntityToCustomerDetailsDto(customer));
        }

        return new Paginated<>(
                customerListResponse,
                page,
                customerListResponse.size(),
                customersList.getTotalPages(),
                customersList.getTotalElements());
    }

    /**
     * @see CustomerService#updateCustomer(Long, UpdateCustomerDto)
     */
    @Override
    public CustomerDetailsDto updateCustomer(Long userId, UpdateCustomerDto updateCustomerDto) {

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
     * @see CustomerService#deleteCustomer(Long)
     */
    @Override
    public void deleteCustomer(Long customerId) {
        LOGGER.debug("Getting customer with id {} from database", customerId);
        UserEntity userEntity = getUserEntityById(customerId);

        LOGGER.debug("Removing customer with id {} from database", customerId);
        try {
            this.userRepository.delete(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while deleting customer with id {} from database", customerId, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }
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
