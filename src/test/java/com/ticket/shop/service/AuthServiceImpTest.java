package com.ticket.shop.service;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.command.auth.ResetPasswordDto;
import com.ticket.shop.command.auth.ResetPasswordTokenDto;
import com.ticket.shop.enumerators.UserRole;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.auth.InvalidTokenException;
import com.ticket.shop.exception.auth.WrongCredentialsException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.UserRepository;
import com.ticket.shop.properties.JwtProperties;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
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
public class AuthServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailServiceImp emailServiceImp;
    private AuthServiceImp authServiceImp;

    private final static String FIRSTNAME = "User";
    private final static String LASTNAME = "Test";
    private final static String EMAIL = "user@service.com";
    private final static String PASSWORD = "Password123";
    private final static String ENCRYPTED_PASSWORD = "321drowssaP";
    private final static Long USER_ID = 10L;
    private final static List<UserRole> USER_ROLE = Collections.singletonList(UserRole.ADMIN);
    private final static String TOKEN = "ajdehjkahnsd";
    private final static Date DATE_TOKEN = new DateTime().plusDays(4).toDate();


    @BeforeEach
    public void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("default");
        jwtProperties.setExpiresInDays(100L);
        this.authServiceImp = new AuthServiceImp(this.userRepository, this.passwordEncoder, jwtProperties, this.emailServiceImp);

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

    /**
     * Validate Reset Password Token Test
     */
    @Test
    public void testValidateResetPasswordTokenSuccessfully() {
        // Mocks
        when(this.userRepository.findByResetPasswordTokenAndResetPasswordExpireTokenIsAfter(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Call method to be tested
        ResetPasswordTokenDto resetPasswordTokenDto = this.authServiceImp.validateResetPassToken(TOKEN);

        // Assert result
        assertNotNull(resetPasswordTokenDto);
        assertEquals(getMockedResetPasswordTokenDto(), resetPasswordTokenDto);
    }

    /**
     * Reset Password Tests
     */
    @Test
    public void testResetPasswordSuccessfully() {
        // Mocks
        when(this.userRepository.findByResetPasswordTokenAndResetPasswordExpireTokenIsAfter(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));
        when(this.passwordEncoder.encode(any())).thenReturn(ENCRYPTED_PASSWORD);

        this.authServiceImp.resetPassword(TOKEN, getMockedResetPasswordDto());

        verify(this.userRepository).save(any());
    }

    @Test
    public void testResetPasswordFailureDueToInvalidResetPasswordTokenException() {
        // Mocks
        when(this.userRepository.findByResetPasswordTokenAndResetPasswordExpireTokenIsAfter(any(), any())).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class,
                () -> this.authServiceImp.resetPassword(TOKEN, getMockedResetPasswordDto()));
    }

    @Test
    public void testResetPasswordFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.userRepository.findByResetPasswordTokenAndResetPasswordExpireTokenIsAfter(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));
        doThrow(RuntimeException.class).when(this.userRepository).save(any());

        assertThrows(DatabaseCommunicationException.class,
                () -> this.authServiceImp.resetPassword(TOKEN, getMockedResetPasswordDto()));
    }

    /**
     * Request Reset Password tests
     */
    @Test
    public void testRequestResetPasswordSuccessfully() {
        // Mocks
        when(this.userRepository.findByEmail(any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Call method to be tested
        this.authServiceImp.requestResetPassword(EMAIL);

        // Assert result
        verify(this.userRepository).save(any());
    }

    @Test
    public void testRequestResetPasswordFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.userRepository.findByEmail(any())).thenReturn(Optional.of(getMockedUserEntity()));
        doThrow(RuntimeException.class).when(this.userRepository).save(any());

        assertThrows(DatabaseCommunicationException.class,
                () -> this.authServiceImp.requestResetPassword(EMAIL));
    }

    /**
     * Confirm email tests
     */
    @Test
    public void testConfirmEmailSuccessfully() {
        // Mocks
        when(this.userRepository.findByConfirmEmailTokenAndConfirmEmailExpireTokenIsAfter(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));

        // Call method to be tested
        this.authServiceImp.confirmEmail(EMAIL);

        // Assert result
        verify(this.userRepository).save(any());
    }

    @Test
    public void testConfirmEmailFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.userRepository.findByConfirmEmailTokenAndConfirmEmailExpireTokenIsAfter(any(), any())).thenReturn(Optional.of(getMockedUserEntity()));
        doThrow(RuntimeException.class).when(this.userRepository).save(any());

        assertThrows(DatabaseCommunicationException.class,
                () -> this.authServiceImp.confirmEmail(EMAIL));
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
                .resetPasswordToken(TOKEN)
                .resetPasswordExpireToken(DATE_TOKEN)
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

    private ResetPasswordTokenDto getMockedResetPasswordTokenDto() {
        return ResetPasswordTokenDto.builder()
                .userId(USER_ID)
                .name(FIRSTNAME)
                .token(TOKEN)
                .build();
    }

    private ResetPasswordDto getMockedResetPasswordDto() {
        return ResetPasswordDto.builder()
                .password(PASSWORD)
                .build();
    }
}
