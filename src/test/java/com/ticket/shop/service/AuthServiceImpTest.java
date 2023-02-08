package com.ticket.shop.service;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.enumerators.UserRoles;
import com.ticket.shop.exception.auth.WrongCredentialsException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.UserRepository;
import com.ticket.shop.properties.JwtProperties;
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

@SpringBootTest
public class AuthServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;
    private AuthServiceImp authServiceImp;

    private final static String FIRSTNAME = "User";
    private final static String LASTNAME = "Test";
    private final static String EMAIL = "user@service.com";
    private final static String PASSWORD = "Password123";
    private final static String ENCRYPTED_PASSWORD = "321drowssaP";
    private final static Long USER_ID = 10L;
    private final static List<UserRoles> USER_ROLE = Collections.singletonList(UserRoles.ADMIN);

    @BeforeEach
    public void setup() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("default");
        jwtProperties.setExpiresInDays(100L);
        this.authServiceImp = new AuthServiceImp(this.userRepository, this.passwordEncoder, jwtProperties);

        // Mocks
        when(this.passwordEncoder.encode(any())).thenReturn(ENCRYPTED_PASSWORD);
    }

    /**
     * Login User Tests
     */
    @Test
    public void testLoginUserSuccessfully() {

        // Mocks
        when(this.userRepository.findByEmail(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        // Call method to be tested
        LoggedInDto loginUser = this.authServiceImp.loginUser(getMockedCredentialsDto());

        // Assert result
        assertNotNull(loginUser);
        assertEquals(getMockedPrincipalDto(), loginUser.getPrincipal());
        assertNotNull(loginUser.getToken());
    }

    @Test
    public void testLoginUserFailureDueToEmailNotFound() {
        // Mocks
        when(this.userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // Assert result
        assertThrows(WrongCredentialsException.class,
                () -> this.authServiceImp.loginUser(getMockedCredentialsDto()));
    }

    @Test
    public void testLoginUserFailureDueToPasswordDontMatch() {
        //Mocks
        when(this.userRepository.findByEmail(any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        // Assert result
        assertThrows(WrongCredentialsException.class,
                () -> this.authServiceImp.loginUser(getMockedCredentialsDto()));
    }

    /**
     * Validate Token Tests
     */
    @Test
    public void testValidateTokenSuccessfully() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.of(getMockedUserEntity()));

        String token = this.authServiceImp.generateJwtToken(getMockedPrincipalDto());

        // Call method to be tested
        PrincipalDto validateToken = this.authServiceImp.validateToken(token);

        // Assert result
        assertNotNull(validateToken);
        assertEquals(getMockedPrincipalDto(), validateToken);
    }

    @Test
    public void testValidateTokenFailureDueToUserNotFound() {
        // Mocks
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        String token = this.authServiceImp.generateJwtToken(getMockedPrincipalDto());

        // Assert result
        assertThrows(UserNotFoundException.class,
                () -> this.authServiceImp.validateToken(token));
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
                .phoneCode("+351")
                .build();
    }

    private CredentialsDto getMockedCredentialsDto() {
        return CredentialsDto.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    private PrincipalDto getMockedPrincipalDto() {
        return PrincipalDto.builder()
                .userId(USER_ID)
                .name(FIRSTNAME + " " + LASTNAME)
                .email(EMAIL)
                .roles(USER_ROLE)
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }
}
