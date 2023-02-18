package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.worker.CreateWorkerDto;
import com.ticket.shop.command.worker.UpdateWorkerDto;
import com.ticket.shop.command.worker.WorkerDetailsDto;
import com.ticket.shop.enumerators.UserRole;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WorkerServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CompanyRepository companyRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;
    private WorkerServiceImp workerServiceImp;

    private final static String FIRSTNAME = "Worker";
    private final static String LASTNAME = "Test";
    private final static String EMAIL = "worker@service.com";
    private final static String PASSWORD = "PasswordWorker!";
    private final static String ENCRYPTED_PASSWORD = "adub1bb891b";
    private final static Long WORKER_ID = 2L;
    private final static Long COMPANY_ID = 245L;
    private final static List<UserRole> USER_ROLE = Collections.singletonList(UserRole.WORKER);

    @BeforeEach
    public void setUp() {
        this.workerServiceImp = new WorkerServiceImp(this.userRepository, this.countryRepository, this.passwordEncoder, this.companyRepository);

        // Mocks
        when(this.passwordEncoder.encode(any())).thenReturn(ENCRYPTED_PASSWORD);
    }

    /**
     * Create worker Tests
     */
    @Test
    public void testCreateWorkerSuccessfully() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenReturn(getMockedUserEntity());

        // Method to be tested
        WorkerDetailsDto workerDetailsDto = this.workerServiceImp.createWorker(getMockedCreateWorkerDto(), COMPANY_ID);

        // Assert result
        assertNotNull(workerDetailsDto);
        assertEquals(getMockedWorkerDetailsDto(), workerDetailsDto);
    }

    @Test
    public void testCreateWorkerFailureDueToCountryNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.workerServiceImp.createWorker(getMockedCreateWorkerDto(), COMPANY_ID));
    }

    @Test
    public void testCreateWorkerFailureDueToCompanyNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CompanyNotFoundException.class,
                () -> this.workerServiceImp.createWorker(getMockedCreateWorkerDto(), COMPANY_ID));
    }

    @Test
    public void testCreateWorkerFailureDueToUserAlreadyExists() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.findByEmail(any()).isPresent()).thenThrow(UserAlreadyExistsException.class);

        // Assert exception
        assertThrows(UserAlreadyExistsException.class,
                () -> this.workerServiceImp.createWorker(getMockedCreateWorkerDto(), COMPANY_ID));
    }

    @Test
    public void testCreateWorkerFailureDueToDatabaseConnectionFailure() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.workerServiceImp.createWorker(getMockedCreateWorkerDto(), COMPANY_ID));
    }

    /**
     * Get worker Tests
     */
    @Test
    public void testGetWorkerSuccessfully() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        when(this.userRepository.findByUserIdAndCompanyId(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Method to be tested
        WorkerDetailsDto workerDetailsDto = this.workerServiceImp.getWorkerById(WORKER_ID, COMPANY_ID);

        // Assert result
        assertNotNull(workerDetailsDto);
        assertEquals(getMockedWorkerDetailsDto(), workerDetailsDto);
    }


    @Test
    public void testGetWorkerFailureDueToUserNotFound() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        when(this.userRepository.findByUserIdAndCompanyId(any(), any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class,
                () -> this.workerServiceImp.getWorkerById(WORKER_ID, COMPANY_ID));
    }

    /**
     * Update worker Tests
     */
    @Test
    public void testUpdateWorkerSuccessfully() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        when(this.userRepository.findByUserIdAndCompanyId(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));

        WorkerDetailsDto worker = WorkerDetailsDto.builder()
                .userId(WORKER_ID)
                .firstname(FIRSTNAME + 11)
                .lastname(LASTNAME + 11)
                .email(EMAIL)
                .roles(List.of(UserRole.COMPANY_ADMIN))
                .countryId(getMockedCountryEntity().getCountryId())
                .companyId(getMockedCompanyEntity().getCompanyId())
                .build();

        UserEntity userEntity = UserEntity.builder()
                .userId(WORKER_ID)
                .firstname(FIRSTNAME + 11)
                .lastname(LASTNAME + 11)
                .email(EMAIL)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .roles(List.of(UserRole.COMPANY_ADMIN))
                .countryEntity(getMockedCountryEntity())
                .companyEntity(getMockedCompanyEntity())
                .build();

        // Method to be tested
        WorkerDetailsDto workerDetailsDto = this.workerServiceImp.updateWorker(COMPANY_ID, WORKER_ID, getMockedUpdateCustomerDto());

        // Assert result
        assertNotNull(workerDetailsDto);
        assertEquals(worker, workerDetailsDto);
        verify(this.userRepository).save(userEntity);
    }

    @Test
    public void testUpdateUserFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        when(this.userRepository.findByUserIdAndCompanyId(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.workerServiceImp.updateWorker(COMPANY_ID, WORKER_ID, getMockedUpdateCustomerDto()));
    }

    @Test
    public void testUpdateUserFailureDueToRoleInvalidException() {

        // Assert exception
        assertThrows(RoleInvalidException.class,
                () -> this.workerServiceImp.updateWorker(COMPANY_ID, WORKER_ID, getMockedUpdateCustomerDtoWithADMIN()));
    }

    @Test
    public void testUpdateUserFailureDueToUserNotFound() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class,
                () -> this.workerServiceImp.updateWorker(COMPANY_ID, WORKER_ID, getMockedUpdateCustomerDto()));
    }

    @Test
    public void testUpdateUserFailureDueToCountryNotFound() {
        // Mocks
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());
        when(this.userRepository.findByUserIdAndCompanyId(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.workerServiceImp.updateWorker(COMPANY_ID, WORKER_ID, getMockedUpdateCustomerDto()));
    }

    /**
     * Get workers list tests
     */
    @Test
    public void testGetWorkersListSuccessfully() {
        //Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        when(this.userRepository.findByCompanyEntity(any(), any())).thenReturn(getMockedPagedUserEntity(0, 1));

        //Call method
        Paginated<WorkerDetailsDto> workerDetailsDto = this.workerServiceImp.getWorkersList(0, 1, getMockedWorkerDetailsDto().getCompanyId());

        //Assert result
        assertNotNull(workerDetailsDto);
        assertEquals(getMockedPaginatedUserDetailsDto(0), workerDetailsDto);
    }

    @Test
    public void testGetWorkersListFailureDueToDatabaseConnectionFailure() {
        //Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        when(this.userRepository.findByCompanyEntity(any(), any())).thenThrow(RuntimeException.class);

        assertThrows(DatabaseCommunicationException.class,
                () -> this.workerServiceImp.getWorkersList(0, 1, getMockedWorkerDetailsDto().getCompanyId()));
    }

    @Test
    public void testGetWorkersListFailureDueToCompanyNotFoundException() {
        //Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> this.workerServiceImp.getWorkersList(0, 1, getMockedWorkerDetailsDto().getCompanyId()));
    }

    private UserEntity getMockedUserEntity() {
        return UserEntity.builder()
                .userId(WORKER_ID)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .roles(USER_ROLE)
                .countryEntity(getMockedCountryEntity())
                .companyEntity(getMockedCompanyEntity())
                .build();
    }

    private CountryEntity getMockedCountryEntity() {
        return CountryEntity.builder()
                .countryId(143L)
                .name("Portugal")
                .isoCode2("PT")
                .isoCode3("PRT")
                .currency("EUR")
                .language("PT")
                .build();
    }

    private WorkerDetailsDto getMockedWorkerDetailsDto() {
        return WorkerDetailsDto.builder()
                .userId(WORKER_ID)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .roles(USER_ROLE)
                .countryId(getMockedCountryEntity().getCountryId())
                .companyId(COMPANY_ID)
                .build();
    }

    private CreateWorkerDto getMockedCreateWorkerDto() {
        return CreateWorkerDto.builder()
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .roles(List.of(UserRole.WORKER))
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }

    private CompanyEntity getMockedCompanyEntity() {
        return CompanyEntity.builder()
                .companyId(COMPANY_ID)
                .name("Company")
                .email("email@a.com")
                .website("website.com")
                .build();
    }

    private UpdateWorkerDto getMockedUpdateCustomerDtoWithADMIN() {
        return UpdateWorkerDto.builder()
                .firstname(FIRSTNAME + 11)
                .lastname(LASTNAME + 11)
                .email(EMAIL)
                .password(PASSWORD + 11)
                .roles(List.of(UserRole.ADMIN, UserRole.COMPANY_ADMIN))
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }

    private UpdateWorkerDto getMockedUpdateCustomerDto() {
        return UpdateWorkerDto.builder()
                .firstname(FIRSTNAME + 11)
                .lastname(LASTNAME + 11)
                .email(EMAIL)
                .password(PASSWORD + 11)
                .roles(List.of(UserRole.COMPANY_ADMIN))
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }

    private Page<UserEntity> getMockedPagedUserEntity(int pageNum, int size) {
        List<UserEntity> content = List.of(getMockedUserEntity());
        Pageable pageable = PageRequest.of(pageNum, size);

        return new PageImpl<>(content, pageable, 1);
    }

    private Paginated<WorkerDetailsDto> getMockedPaginatedUserDetailsDto(int page) {
        List<WorkerDetailsDto> patientDetailsDtos = List.of(getMockedWorkerDetailsDto());

        return new Paginated<>(
                patientDetailsDtos,
                page,
                patientDetailsDtos.size(),
                1,
                1);
    }
}
