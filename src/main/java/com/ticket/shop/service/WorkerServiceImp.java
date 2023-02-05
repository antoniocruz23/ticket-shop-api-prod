package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.worker.CreateWorkerDto;
import com.ticket.shop.command.worker.UpdateWorkerDto;
import com.ticket.shop.command.worker.WorkerDetailsDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.enumerators.UserRoles;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * WorkerService Implementation
 */
@Service
public class WorkerServiceImp implements WorkerService {

    private static final Logger LOGGER = LogManager.getLogger(CustomerService.class);
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public WorkerServiceImp(UserRepository userRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
    }

    /**
     * @see WorkerService#createWorker(CreateWorkerDto, Long)
     */
    @Override
    public WorkerDetailsDto createWorker(CreateWorkerDto createWorkerDto, Long companyId) throws UserAlreadyExistsException {

        LOGGER.debug("Creating worker - {}", createWorkerDto);
        UserEntity userEntity = UserConverter.fromCreateWorkerDtoToUserEntity(createWorkerDto);

        if (userEntity.getRoles().contains(UserRoles.ADMIN)) {
            LOGGER.debug("Failed while trying to create the worker role with ADMIN role");
            throw new RoleInvalidException(ErrorMessages.ROLE_INVALID);
        }

        if (this.userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            LOGGER.error("Duplicated email - {}", userEntity.getEmail());
            throw new UserAlreadyExistsException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        CompanyEntity companyEntity = getCompanyEntityById(companyId);
        userEntity.setCompanyEntity(companyEntity);

        CountryEntity countryEntity = getCountryEntityById(createWorkerDto.getCountryId());
        userEntity.setCountryEntity(countryEntity);

        String encryptedPassword = this.passwordEncoder.encode(createWorkerDto.getPassword());
        userEntity.setEncryptedPassword(encryptedPassword);

        LOGGER.info("Persisting worker into database");
        UserEntity createdWorker;
        try {
            LOGGER.info("Saving worker on database");
            createdWorker = this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving worker into database {}", userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created worker");
        return UserConverter.fromUserEntityToWorkerDetailsDto(createdWorker);
    }

    /**
     * @see WorkerService#getWorkerById(Long, Long)
     */
    @Override
    public WorkerDetailsDto getWorkerById(Long workerId, Long companyId) throws UserNotFoundException {
        UserEntity userEntity = getWorkerByIdAndCompany(workerId, companyId);

        return UserConverter.fromUserEntityToWorkerDetailsDto(userEntity);
    }

    /**
     * @see WorkerService#getWorkersList(int, int, Long)
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
     * @see WorkerService#updateWorker(Long, Long, UpdateWorkerDto)
     */
    @Override
    public WorkerDetailsDto updateWorker(Long companyId, Long userId, UpdateWorkerDto updateUserDto) throws UserNotFoundException {

        if (updateUserDto.getRoles().contains(UserRoles.ADMIN)) {
            LOGGER.debug("Failed while trying to update the worker role to application ADMIN");
            throw new RoleInvalidException(ErrorMessages.ROLE_INVALID);
        }

        UserEntity userEntity = getWorkerByIdAndCompany(userId, companyId);
        CountryEntity countryEntity = getCountryEntityById(updateUserDto.getCountryId());
        String encryptedPassword = this.passwordEncoder.encode(updateUserDto.getPassword());

        userEntity.setFirstname(updateUserDto.getFirstname());
        userEntity.setLastname(updateUserDto.getLastname());
        userEntity.setEmail(updateUserDto.getEmail());
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setRoles(updateUserDto.getRoles());
        userEntity.setCountryEntity(countryEntity);

        LOGGER.debug("Updating customer with id {} with new data", userId);
        try {
            this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while updating customer with id {} with new data - {}", userId, userEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return UserConverter.fromUserEntityToWorkerDetailsDto(userEntity);
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
     * @param workerId  worker id
     * @param companyId company id
     * @return {@link UserEntity}
     */
    private UserEntity getWorkerByIdAndCompany(Long workerId, Long companyId) {
        LOGGER.debug("Getting worker with id {} from database", workerId);
        return this.userRepository.findByUserIdAndCompanyId(workerId, companyId)
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
}
