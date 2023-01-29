package com.ticket.shop.service;


import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
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

    @Override
    public UserDetailsDto createUser(CreateUserDto createUserDto) throws UserAlreadyExistsException {

        LOGGER.debug("Creating user - {}", createUserDto);
        UserEntity userEntity = UserConverter.fromCreateUserDtoToUserEntity(createUserDto);

        CountryEntity countryEntity = getCountryEntityById(createUserDto.getCountryId());
        userEntity.setCountryEntity(countryEntity);

        String encryptedPassword = passwordEncoder.encode(createUserDto.getPassword());
        userEntity.setEncryptedPassword(encryptedPassword);

        if (userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {

            LOGGER.error("Duplicated email - {}", userEntity.getEmail());
            throw new UserAlreadyExistsException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        LOGGER.info("Persisting user into database");
        UserEntity createdUser;
        try {
            LOGGER.info("Saving user on database");
            createdUser = userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving user into database {}", userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created user");
        return UserConverter.fromUserEntityToUserDetailsDto(createdUser);
    }

    /**
     * Get Country by id
     * @param countryId country id
     * @return {@link CountryEntity}
     */
    protected CountryEntity getCountryEntityById(long countryId) {
        LOGGER.debug("Verifying if country with id {} exists", countryId);
        return countryRepository.findById(countryId)
                .orElseThrow(() -> {
                    LOGGER.error("Country with id {} doesn't exist", countryId);
                    return new CountryNotFoundException(ErrorMessages.COUNTRY_NOT_FOUND);
                });
    }
}
