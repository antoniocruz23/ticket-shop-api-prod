package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.worker.CreateWorkerDto;
import com.ticket.shop.command.worker.UpdateWorkerDto;
import com.ticket.shop.command.worker.WorkerDetailsDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.enumerators.UserRole;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.auth.RoleInvalidException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An {@link WorkerService} implementation
 */
@Service
public class WorkerServiceImp implements WorkerService {

    private static final Logger LOGGER = LogManager.getLogger(WorkerService.class);
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthServiceImp authServiceImp;
    private final EmailServiceImp emailServiceImp;

    @Value("${ticket-shop.resetPassToken.expiresInHours}")
    private long expiresInHours;

    public WorkerServiceImp(UserRepository userRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder, CompanyRepository companyRepository, AuthServiceImp authServiceImp, EmailServiceImp emailServiceImp) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
        this.authServiceImp = authServiceImp;
        this.emailServiceImp = emailServiceImp;
    }

    /**
     * @see WorkerService#createWorker(Long, CreateWorkerDto)
     */
    @Override
    public WorkerDetailsDto createWorker(Long companyId, CreateWorkerDto createWorkerDto)
            throws UserAlreadyExistsException, CompanyNotFoundException, CountryNotFoundException, RoleInvalidException {

        if (this.userRepository.findByEmail(createWorkerDto.getEmail()).isPresent()) {
            LOGGER.error("Duplicated email - {}", createWorkerDto.getEmail());
            throw new UserAlreadyExistsException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        UserEntity userEntity = buildUserEntity(companyId, createWorkerDto);

        LOGGER.info("Persisting worker into database");
        UserEntity createdWorker;
        try {
            LOGGER.info("Saving worker on database");
            createdWorker = this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving worker into database {}", userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        this.emailServiceImp.sendEmailToConfirmEmailAddress(
                createdWorker.getFirstname(),
                createdWorker.getEmail(),
                createdWorker.getConfirmEmailToken(),
                this.expiresInHours
        );

        LOGGER.debug("Retrieving created worker");
        return UserConverter.fromUserEntityToWorkerDetailsDto(createdWorker);
    }

    private UserEntity buildUserEntity(Long companyId, CreateWorkerDto createWorkerDto) {
        LOGGER.debug("Creating worker - {}", createWorkerDto);
        UserEntity userEntity = UserConverter.fromCreateWorkerDtoToUserEntity(createWorkerDto);

        validateRoles(userEntity.getRoles());

        CompanyEntity companyEntity = getCompanyEntityById(companyId);
        userEntity.setCompanyEntity(companyEntity);

        CountryEntity countryEntity = getCountryEntityById(createWorkerDto.getCountryId());
        userEntity.setCountryEntity(countryEntity);

        String encryptedPassword = this.passwordEncoder.encode(createWorkerDto.getPassword());
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
     * @see WorkerService#getWorkerById(Long, Long)
     */
    @Override
    public WorkerDetailsDto getWorkerById(Long companyId, Long workerId) throws UserNotFoundException {
        UserEntity userEntity = getWorkerByIdAndCompany(companyId, workerId);

        return UserConverter.fromUserEntityToWorkerDetailsDto(userEntity);
    }

    /**
     * @see WorkerService#getWorkersList(Long, int, int)
     */
    @Override
    public Paginated<WorkerDetailsDto> getWorkersList(Long companyId, int page, int size) {

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
     * @see WorkerService#updateWorker(Long, Long, UpdateWorkerDto)
     */
    @Override
    public WorkerDetailsDto updateWorker(Long companyId, Long userId, UpdateWorkerDto updateUserDto) throws UserNotFoundException, CountryNotFoundException {

        validateRoles(updateUserDto.getRoles());

        UserEntity userEntity = getWorkerByIdAndCompany(companyId, userId);
        CountryEntity countryEntity = getCountryEntityById(updateUserDto.getCountryId());
        String encryptedPassword = this.passwordEncoder.encode(updateUserDto.getPassword());

        userEntity.setFirstname(updateUserDto.getFirstname());
        userEntity.setLastname(updateUserDto.getLastname());
        userEntity.setEmail(updateUserDto.getEmail());
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setRoles(updateUserDto.getRoles());
        userEntity.setCountryEntity(countryEntity);

        LOGGER.debug("Updating worker with id {} with new data", userId);
        try {
            this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while updating worker with id {} with new data - {}", userId, userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return UserConverter.fromUserEntityToWorkerDetailsDto(userEntity);
    }

    /**
     * @see WorkerService#deleteWorker(Long, Long)
     */
    @Override
    public void deleteWorker(Long companyId, Long workerId) throws UserNotFoundException {
        LOGGER.debug("Getting worker with id {} from database", workerId);
        UserEntity userEntity = getWorkerByIdAndCompany(companyId, workerId);

        LOGGER.debug("Removing worker with id {} from database", workerId);
        try {
            this.userRepository.delete(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while deleting worker with id {} from database", workerId, e);
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
     * Get Worker by id and company
     *
     * @param companyId company id
     * @param workerId  worker id
     * @return {@link UserEntity}
     */
    private UserEntity getWorkerByIdAndCompany(Long companyId, Long workerId) {
        LOGGER.debug("Getting worker with id {} from database", workerId);
        return this.userRepository.findByUserIdAndCompanyId(companyId, workerId)
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
    private CompanyEntity getCompanyEntityById(Long companyId) {
        LOGGER.debug("Getting company with id {} from database", companyId);
        return this.companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    LOGGER.error("The company with id {} does not exist in database", companyId);
                    return new CompanyNotFoundException(ErrorMessages.COMPANY_NOT_FOUND);
                });
    }

    /**
     * Valida role
     *
     * @param roles list of roles
     */
    private static void validateRoles(List<UserRole> roles) {
        if (roles.contains(UserRole.ADMIN) || roles.contains(UserRole.CUSTOMER)) {
            LOGGER.debug("Failed while trying to create the worker with an invalid role for workers");
            throw new RoleInvalidException(ErrorMessages.ROLE_INVALID);
        }
    }
}
