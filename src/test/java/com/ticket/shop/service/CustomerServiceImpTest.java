package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.command.customer.UpdateCustomerDto;
import com.ticket.shop.enumerators.UserRole;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.UserEntity;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomerServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;
    private CustomerServiceImp customerServiceImp;

    @Mock
    private AuthServiceImp authServiceImp;

    @Mock
    private EmailServiceImp emailServiceImp;

    private final static String FIRSTNAME = "customer";
    private final static String LASTNAME = "Test";
    private final static String EMAIL = "test@service.com";
    private final static String PASSWORD = "Password123!";
    private final static String ENCRYPTED_PASSWORD = "adub1bb891b";
    private final static Long CUSTOMER_ID = 3L;
    private final static List<UserRole> USER_ROLE = Collections.singletonList(UserRole.CUSTOMER);

    @BeforeEach
    public void setUp() {
        this.customerServiceImp = new CustomerServiceImp(this.userRepository, this.countryRepository, this.passwordEncoder, this.authServiceImp, this.emailServiceImp);

        // Mocks
        when(this.passwordEncoder.encode(any())).thenReturn(ENCRYPTED_PASSWORD);
    }

    /**
     * Create customer Tests
     */
    @Test
    public void testCreateCustomerSuccessfully() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenReturn(getMockedUserEntity());

        // Method to be tested
        CustomerDetailsDto customerDetails = this.customerServiceImp.createCustomer(getMockedCreateCustomerDto());

        // Assert result
        assertNotNull(customerDetails);
        assertEquals(getMockedCustomerDetailsDto(), customerDetails);
    }

    @Test
    public void testCreateCustomerFailureDueToCountryNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.customerServiceImp.createCustomer(getMockedCreateCustomerDto()));
    }

    @Test
    public void testCreateCustomerFailureDueToUserAlreadyExists() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.findByEmail(any()).isPresent()).thenThrow(UserAlreadyExistsException.class);

        // Assert exception
        assertThrows(UserAlreadyExistsException.class,
                () -> this.customerServiceImp.createCustomer(getMockedCreateCustomerDto()));
    }

    @Test
    public void testCreateCustomerFailureDueToDatabaseConnectionFailure() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.customerServiceImp.createCustomer(getMockedCreateCustomerDto()));
    }

    /**
     * Get customer Tests
     */
    @Test
    public void testGetCustomerSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Method to be tested
        CustomerDetailsDto customerDetails = this.customerServiceImp.getCustomerById(CUSTOMER_ID);

        // Assert result
        assertNotNull(customerDetails);
        assertEquals(getMockedCustomerDetailsDto(), customerDetails);
    }


    @Test
    public void testGetCustomerFailureDueToUserNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class,
                () -> this.customerServiceImp.getCustomerById(CUSTOMER_ID));
    }

    /**
     * Get customer list tests
     */
    @Test
    public void testGetCustomerListSuccessfully() {
        //Mocks
        when(this.userRepository.findByRolesContains(any(), any())).thenReturn(getMockedPagedUserEntity());

        //Call method
        Paginated<CustomerDetailsDto> customerDetailsDto = this.customerServiceImp.getCustomersList(0, 1);

        //Assert result
        assertNotNull(customerDetailsDto);
        assertEquals(getMockedPaginatedUserDetailsDto(), customerDetailsDto);
    }

    @Test
    public void testGetCustomerListFailureDueToDatabaseConnectionFailure() {
        //Mocks
        when(this.userRepository.findByRolesContains(any(), any())).thenThrow(RuntimeException.class);

        assertThrows(DatabaseCommunicationException.class,
                () -> this.customerServiceImp.getCustomersList(0, 1));
    }

    /**
     * Update customer Tests
     */
    @Test
    public void testUpdateCustomerSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        CustomerDetailsDto user = CustomerDetailsDto.builder().userId(CUSTOMER_ID).firstname(FIRSTNAME + 11).lastname(LASTNAME + 11).email(EMAIL).countryId(1L).build();
        UserEntity userEntity = UserEntity.builder()
                .userId(CUSTOMER_ID)
                .firstname(FIRSTNAME + 11)
                .lastname(LASTNAME + 11)
                .email(EMAIL)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .roles(List.of(UserRole.CUSTOMER))
                .countryEntity(getMockedCountryEntity()).build();

        // Method to be tested
        CustomerDetailsDto customerDetails = this.customerServiceImp.updateCustomer(CUSTOMER_ID, getMockedUpdateCustomerDto());

        // Assert result
        assertNotNull(customerDetails);
        assertEquals(user, customerDetails);
        verify(this.userRepository).save(userEntity);
    }

    @Test
    public void testUpdateCustomerFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.customerServiceImp.updateCustomer(CUSTOMER_ID, getMockedUpdateCustomerDto()));
    }

    @Test
    public void testUpdateCustomerFailureDueToUserNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class,
                () -> this.customerServiceImp.updateCustomer(CUSTOMER_ID, getMockedUpdateCustomerDto()));
    }

    @Test
    public void testUpdateCustomerFailureDueToCountryNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.customerServiceImp.updateCustomer(CUSTOMER_ID, getMockedUpdateCustomerDto()));
    }

    /**
     * Delete customer tests
     */
    @Test
    public void testDeleteCustomerSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Call method to be tested
        this.customerServiceImp.deleteCustomer(CUSTOMER_ID);

        verify(this.userRepository).delete(any());
    }

    @Test
    public void testDeleteCustomerFailureDueToUserNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> this.customerServiceImp.deleteCustomer(CUSTOMER_ID));
    }

    @Test
    public void testDeleteCustomerFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        doThrow(RuntimeException.class).when(this.userRepository).delete(any());

        assertThrows(DatabaseCommunicationException.class,
                () -> this.customerServiceImp.deleteCustomer(CUSTOMER_ID));
    }

    private UserEntity getMockedUserEntity() {
        return UserEntity.builder()
                .userId(CUSTOMER_ID)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .roles(USER_ROLE)
                .countryEntity(getMockedCountryEntity())
                .build();
    }

    private CountryEntity getMockedCountryEntity() {
        return CountryEntity.builder()
                .countryId(1L)
                .name("Portugal")
                .isoCode2("PT")
                .isoCode3("PRT")
                .currency("EUR")
                .language("PT")
                .build();
    }

    private CustomerDetailsDto getMockedCustomerDetailsDto() {
        return CustomerDetailsDto.builder()
                .userId(CUSTOMER_ID)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .countryId(1L)
                .build();
    }

    private CreateCustomerDto getMockedCreateCustomerDto() {
        return CreateCustomerDto.builder()
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .countryId(1L)
                .build();
    }

    private UpdateCustomerDto getMockedUpdateCustomerDto() {
        return UpdateCustomerDto.builder()
                .firstname(FIRSTNAME + "11")
                .lastname(LASTNAME + "11")
                .email(EMAIL)
                .password(PASSWORD + "11")
                .countryId(1L)
                .build();
    }

    private Page<UserEntity> getMockedPagedUserEntity() {
        List<UserEntity> content = List.of(getMockedUserEntity());
        Pageable pageable = PageRequest.of(0, 1);

        return new PageImpl<>(content, pageable, 1);
    }

    private Paginated<CustomerDetailsDto> getMockedPaginatedUserDetailsDto() {
        List<CustomerDetailsDto> workerDetailsDtos = List.of(getMockedCustomerDetailsDto());

        return new Paginated<>(
                workerDetailsDtos,
                0,
                workerDetailsDtos.size(),
                1,
                1);
    }
}
