package com.ticket.shop.service;

import com.ticket.shop.command.user.CreateUserDto;
import com.ticket.shop.command.user.UpdateUserDto;
import com.ticket.shop.command.user.UserDetailsDto;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;
    private UserServiceImp userServiceImp;

    private final static String FIRSTNAME = "User";
    private final static String LASTNAME = "Test";
    private final static String EMAIL = "test@service.com";
    private final static String PASSWORD = "Password123!";
    private final static String ENCRYPTED_PASSWORD = "adub1bb891b";
    private final static Long USER_ID = 3L;
    private final static List<UserRoles> USER_ROLE = Collections.singletonList(UserRoles.ADMIN);

    @BeforeEach
    public void setup() {
        this.userServiceImp = new UserServiceImp(this.userRepository, this.countryRepository, this.passwordEncoder);

        // Mocks
        when(this.passwordEncoder.encode(any())).thenReturn(ENCRYPTED_PASSWORD);
    }

    /**
     * Create User Tests
     */
    @Test
    public void testCreateUserSuccessfully() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenReturn(getMockedUserEntity());

        // Method to be tested
        UserDetailsDto userDetails = this.userServiceImp.createCustomer(getMockedCreateUserDto());

        // Assert result
        assertNotNull(userDetails);
        assertEquals(getMockedUserDetailsDto(), userDetails);
    }

    @Test
    public void testCreateUserFailureDueToCountryNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.userServiceImp.createCustomer(getMockedCreateUserDto()));
    }

    @Test
    public void testCreateUserFailureDueToUserAlreadyExists() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.findByEmail(any()).isPresent()).thenThrow(UserAlreadyExistsException.class);

        // Assert exception
        assertThrows(UserAlreadyExistsException.class,
                () -> this.userServiceImp.createCustomer(getMockedCreateUserDto()));
    }

    @Test
    public void testCreateUserFailureDueToDatabaseConnectionFailure() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.userServiceImp.createCustomer(getMockedCreateUserDto()));
    }

    /**
     * Get User Tests
     */
    @Test
    public void testGetUserSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Method to be tested
        UserDetailsDto userDetails = this.userServiceImp.getUserById(USER_ID);

        // Assert result
        assertNotNull(userDetails);
        assertEquals(getMockedUserDetailsDto(), userDetails);
    }


    @Test
    public void testGetUserFailureDueToUserNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class,
                () -> this.userServiceImp.getUserById(USER_ID));
    }

    /**
     * Update User Tests
     */
    @Test
    public void testUpdateUserSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        UserDetailsDto user = UserDetailsDto.builder().userId(USER_ID).firstname(FIRSTNAME + 11).lastname(LASTNAME + 11).email(EMAIL).build();
        UserEntity userEntity = UserEntity.builder()
                .userId(USER_ID)
                .firstname(FIRSTNAME + 11)
                .lastname(LASTNAME + 11)
                .email(EMAIL)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .roles(List.of(UserRoles.ADMIN, UserRoles.CUSTOMER))
                .countryEntity(getMockedCountryEntity()).build();

        // Method to be tested
        UserDetailsDto userDetails = this.userServiceImp.updateUser(USER_ID, getMockedUpdateUserDto());

        // Assert result
        assertNotNull(userDetails);
        assertEquals(user, userDetails);
        verify(this.userRepository).save(userEntity);
    }

    @Test
    public void testUpdateUserFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.userServiceImp.updateUser(USER_ID, getMockedUpdateUserDto()));
    }

    @Test
    public void testUpdateUserFailureDueToUserNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class,
                () -> this.userServiceImp.updateUser(USER_ID, getMockedUpdateUserDto()));
    }

    @Test
    public void testUpdateUserFailureDueToCountryNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.userServiceImp.updateUser(USER_ID, getMockedUpdateUserDto()));
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

    private UserDetailsDto getMockedUserDetailsDto() {
        return UserDetailsDto.builder()
                .userId(USER_ID)
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .build();
    }

    private CreateUserDto getMockedCreateUserDto() {
        return CreateUserDto.builder()
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .roles(USER_ROLE)
                .countryId(1L)
                .build();
    }

    private UpdateUserDto getMockedUpdateUserDto() {
        return UpdateUserDto.builder()
                .firstname(FIRSTNAME + "11")
                .lastname(LASTNAME + "11")
                .email(EMAIL)
                .password(PASSWORD + "11")
                .roles(List.of(UserRoles.ADMIN, UserRoles.CUSTOMER))
                .countryId(1L)
                .build();
    }
}
