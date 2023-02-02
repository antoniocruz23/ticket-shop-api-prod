package com.ticket.shop.service;


import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UpdateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.enumerators.UserRoles;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.CompanyEntity;
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
public class UserServiceImp implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @see UserService#createCustomer(CreateUserDto)
     */
    @Override
    public UserDetailsDto createCustomer(CreateUserDto createUserDto) throws UserAlreadyExistsException {

        LOGGER.debug("Creating user - {}", createUserDto);
        UserEntity userEntity = UserConverter.fromCreateUserDtoToUserEntity(createUserDto);

        userEntity.setRoles(List.of(UserRoles.CUSTOMER));

        return createUser(userEntity, createUserDto.getCountryId(), createUserDto.getPassword());
    }

    /**
     * @see UserService#createWorker(CreateUserDto, Long)
     */
    @Override
    public UserDetailsDto createWorker(CreateUserDto createUserDto, Long userId) throws UserAlreadyExistsException {

        LOGGER.debug("Creating user - {}", createUserDto);
        UserEntity userEntity = UserConverter.fromCreateUserDtoToUserEntity(createUserDto);

        CompanyEntity companyEntity = getUserEntityById(userId).getCompanyEntity();
        userEntity.setCompanyEntity(companyEntity);
        userEntity.setRoles(List.of(UserRoles.WORKER));

        return createUser(userEntity, createUserDto.getCountryId(), createUserDto.getPassword());
    }

    private UserDetailsDto createUser(UserEntity userEntity, Long countryId, String password) {
        CountryEntity countryEntity = getCountryEntityById(countryId);
        userEntity.setCountryEntity(countryEntity);

        String encryptedPassword = this.passwordEncoder.encode(password);
        userEntity.setEncryptedPassword(encryptedPassword);

        if (this.userRepository.findByEmail(userEntity.getEmail()).isPresent()) {

            LOGGER.error("Duplicated email - {}", userEntity.getEmail());
            throw new UserAlreadyExistsException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        LOGGER.info("Persisting user into database");
        UserEntity createdUser;
        try {
            LOGGER.info("Saving user on database");
            createdUser = this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving user into database {}", userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created user");
        return UserConverter.fromUserEntityToUserDetailsDto(createdUser);
    }

    /**
     * @see UserService#getUserById(Long)
     */
    @Override
    public UserDetailsDto getUserById(Long userId) throws UserNotFoundException {
        return UserConverter.fromUserEntityToUserDetailsDto(getUserEntityById(userId));
    }

    /**
     * @see UserService#updateUser(Long, UpdateUserDto)
     */
    @Override
    public UserDetailsDto updateUser(Long userId, UpdateUserDto updateUserDto) throws UserNotFoundException {

        UserEntity userEntity = getUserEntityById(userId);
        CountryEntity countryEntity = getCountryEntityById(updateUserDto.getCountryId());
        String encryptedPassword = this.passwordEncoder.encode(updateUserDto.getPassword());

        userEntity.setFirstname(updateUserDto.getFirstname());
        userEntity.setLastname(updateUserDto.getLastname());
        userEntity.setEmail(updateUserDto.getEmail());
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setRoles(updateUserDto.getRoles());
        userEntity.setCountryEntity(countryEntity);

        LOGGER.debug("Updating user with id {} with new data", userId);
        try {
            this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while updating user with id {} with new data - {}", userId, userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return UserConverter.fromUserEntityToUserDetailsDto(userEntity);
    }

    /**
     * Get Country by id
     *
     * @param countryId country id
     * @return {@link CountryEntity}
     */
    protected CountryEntity getCountryEntityById(Long countryId) {
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
    protected UserEntity getUserEntityById(Long userId) {
        LOGGER.debug("Getting user with id {} from database", userId);
        return this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("The user with id {} does not exist in database", userId);
                    return new UserNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });
    }
}
