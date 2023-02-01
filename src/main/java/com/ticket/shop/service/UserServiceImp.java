package com.ticket.shop.service;


import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UpdateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.converter.UserConverter;
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
     * @see UserService#createUser(CreateUserDto)
     */
    @Override
    public UserDetailsDto createUser(CreateUserDto createUserDto) throws UserAlreadyExistsException {

        LOGGER.debug("Creating user - {}", createUserDto);
        UserEntity userEntity = UserConverter.fromCreateUserDtoToUserEntity(createUserDto);

        CountryEntity countryEntity = getCountryEntityById(createUserDto.getCountryId());
        userEntity.setCountryEntity(countryEntity);

        String encryptedPassword = this.passwordEncoder.encode(createUserDto.getPassword());
        userEntity.setEncryptedPassword(encryptedPassword);

        if (userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {

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
