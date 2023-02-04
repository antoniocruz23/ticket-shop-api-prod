package com.ticket.shop.service;

import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.command.customer.UpdateCustomerDto;
import com.ticket.shop.enumerators.UserRoles;
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
public class CustomerServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;
    private CustomerServiceImp userServiceImp;

    private final static String FIRSTNAME = "customer";
    private final static String LASTNAME = "Test";
    private final static String EMAIL = "test@service.com";
    private final static String PASSWORD = "Password123!";
    private final static String ENCRYPTED_PASSWORD = "adub1bb891b";
    private final static Long USER_ID = 3L;
    private final static List<UserRoles> USER_ROLE = Collections.singletonList(UserRoles.CUSTOMER);

    @BeforeEach
    public void setup() {
        this.userServiceImp = new CustomerServiceImp(this.userRepository, this.countryRepository, this.passwordEncoder);

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
        CustomerDetailsDto customerDetails = this.userServiceImp.createCustomer(getMockedCreateCustomerDto());

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
                () -> this.userServiceImp.createCustomer(getMockedCreateCustomerDto()));
    }

    @Test
    public void testCreateCustomerFailureDueToUserAlreadyExists() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.findByEmail(any()).isPresent()).thenThrow(UserAlreadyExistsException.class);

        // Assert exception
        assertThrows(UserAlreadyExistsException.class,
                () -> this.userServiceImp.createCustomer(getMockedCreateCustomerDto()));
    }

    @Test
    public void testCreateCustomerFailureDueToDatabaseConnectionFailure() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.userServiceImp.createCustomer(getMockedCreateCustomerDto()));
    }

    /**
     * Get customer Tests
     */
    @Test
    public void testGetCustomerSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Method to be tested
        CustomerDetailsDto customerDetails = this.userServiceImp.getCustomerById(USER_ID);

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
                () -> this.userServiceImp.getCustomerById(USER_ID));
    }

    /**
     * Update customer Tests
     */
    @Test
    public void testUpdateCustomerSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        CustomerDetailsDto user = CustomerDetailsDto.builder().userId(USER_ID).firstname(FIRSTNAME + 11).lastname(LASTNAME + 11).email(EMAIL).countryId(1L).build();
        UserEntity userEntity = UserEntity.builder()
                .userId(USER_ID)
                .firstname(FIRSTNAME + 11)
                .lastname(LASTNAME + 11)
                .email(EMAIL)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .roles(List.of(UserRoles.ADMIN, UserRoles.CUSTOMER))
                .countryEntity(getMockedCountryEntity()).build();

        // Method to be tested
        CustomerDetailsDto customerDetails = this.userServiceImp.updateCustomer(USER_ID, getMockedUpdateCustomerDto());

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
                () -> this.userServiceImp.updateCustomer(USER_ID, getMockedUpdateCustomerDto()));
    }

    @Test
    public void testUpdateCustomerFailureDueToUserNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class,
                () -> this.userServiceImp.updateCustomer(USER_ID, getMockedUpdateCustomerDto()));
    }

    @Test
    public void testUpdateCustomerFailureDueToCountryNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.userServiceImp.updateCustomer(USER_ID, getMockedUpdateCustomerDto()));
    }

    private UserEntity getMockedUserEntity() {
        return UserEntity.builder()
                .userId(USER_ID)
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
                .userId(USER_ID)
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
                .roles(List.of(UserRoles.ADMIN, UserRoles.CUSTOMER))
                .countryId(1L)
                .build();
    }
}
