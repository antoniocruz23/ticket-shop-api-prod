package com.ticket.shop.service;


import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UpdateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
import com.ticket.shop.command.user.WorkerDetailsDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.enumerators.UserRoles;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
import com.ticket.shop.persistence.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * UserService Implementation
 */
@Service
public class UserServiceImp implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
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

    /**
     * Create user method common between createCustomer and createWorker
     *
     * @param userEntity userEntity
     * @param countryId  countryId
     * @param password   password to be encrypted
     * @return {@link UserDetailsDto}
     */
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
     * @see UserService#getWorkerById(Long, Long)
     */
    @Override
    public WorkerDetailsDto getWorkerById(Long workerId, Long companyId) throws UserNotFoundException {
        CompanyEntity companyEntity = getCompanyEntityById(companyId);
        UserEntity userEntity = getWorkerByIdAndCompany(workerId, companyEntity);

        return UserConverter.fromUserEntityToWorkerDetailsDto(userEntity);
    }

    /**
     * @see UserService#getWorkersList(int, int, Long)
     */
    @Override
    public Paginated<WorkerDetailsDto> getWorkersList(int page, int size, Long companyId) {

        CompanyEntity companyEntity = getCompanyEntityById(companyId);

        LOGGER.debug("Getting all patients from database");
        Page<UserEntity> workersList;
        try {
            workersList = this.userRepository.findByCompanyEntity(companyEntity, PageRequest.of(page, size));

        } catch (Exception e) {
            LOGGER.error("Failed at getting workers page from database", e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Converting workers list to WorkerDetailsDto");
        List<WorkerDetailsDto> patientListResponse = new ArrayList<>();
        for (UserEntity worker : workersList) {
            patientListResponse.add(UserConverter.fromUserEntityToWorkerDetailsDto(worker));
        }

        return new Paginated<>(
                patientListResponse,
                page,
                patientListResponse.size(),
                workersList.getTotalPages(),
                workersList.getTotalElements());
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

    /**
     * Get Worker by id and company
     *
     * @param workerId      worker id
     * @param companyEntity company entity
     * @return {@link UserEntity}
     */
    protected UserEntity getWorkerByIdAndCompany(Long workerId, CompanyEntity companyEntity) {
        LOGGER.debug("Getting worker with id {} from database", workerId);
        return this.userRepository.findByUserIdAndCompanyEntity(workerId, companyEntity)
                .orElseThrow(() -> {
                    LOGGER.error("The worker with id {} does not exist in database", workerId);
                    return new UserNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });
    }

    /**
     * Get Company by id
     *
     * @param companyId company id
     * @return {@link CompanyEntity}
     */
    protected CompanyEntity getCompanyEntityById(Long companyId) {
        LOGGER.debug("Getting company with id {} from database", companyId);
        return this.companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    LOGGER.error("The company with id {} does not exist in database", companyId);
                    return new CompanyNotFoundException(ErrorMessages.COMPANY_NOT_FOUND);
                });
    }
}
