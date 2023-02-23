package com.ticket.shop.service;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.command.auth.ResetPasswordDto;
import com.ticket.shop.command.auth.ResetPasswordTokenDto;
import com.ticket.shop.command.email.EmailDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.enumerators.EmailTemplate;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.auth.InvalidTokenException;
import com.ticket.shop.exception.auth.WrongCredentialsException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.UserRepository;
import com.ticket.shop.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * An {@link AuthService} implementation
 */
@Service
public class AuthServiceImp implements AuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImp.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final String signatureAlgorithm = SignatureAlgorithm.HS256.getJcaName();
    private final String secretKey = Base64.getEncoder().withoutPadding().encodeToString(new byte[256]);
    private Key signingKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary(this.secretKey), this.signatureAlgorithm);
    private final EmailServiceImp emailServiceImp;

    @Value("${ticket-shop.resetPassToken.expiresInHours}")
    private long expiresInHours;

    public AuthServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProperties jwtProperties, EmailServiceImp emailServiceImp) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
        this.emailServiceImp = emailServiceImp;
    }

    @PostConstruct
    protected void init() {
        String secretKey = Base64.getEncoder().encodeToString(this.jwtProperties.getSecretKey().getBytes());
        this.signingKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), this.signatureAlgorithm);
    }

    /**
     * @see AuthService#loginUser(CredentialsDto)
     */
    @Override
    public LoggedInDto loginUser(CredentialsDto credentialsDto) {

        UserEntity userEntity =
                this.userRepository.findByEmail(credentialsDto.getEmail())
                        .orElseThrow(() -> {
                            LOGGER.error("User with email {} not found on database", credentialsDto.getEmail());
                            return new WrongCredentialsException(ErrorMessages.WRONG_CREDENTIALS);
                        });

        boolean passwordMatches = this.passwordEncoder.matches(credentialsDto.getPassword(), userEntity.getEncryptedPassword());
        if (!passwordMatches) {
            LOGGER.error("The password doesn't match");
            throw new WrongCredentialsException(ErrorMessages.WRONG_CREDENTIALS);
        }

        PrincipalDto principal = UserConverter.fromUserEntityToPrincipalDto(userEntity);

        LOGGER.info("Generating JWT token for the user with id {} ...", userEntity.getUserId());
        String token = generateJwtToken(principal);

        return LoggedInDto.builder()
                .principal(principal)
                .token(token)
                .build();
    }

    /**
     * @see AuthService#validateToken(String)
     */
    @Override
    public PrincipalDto validateToken(String token) {
        Jws<Claims> jwtClaims = Jwts.parserBuilder()
                .setSigningKey(this.signingKey)
                .build()
                .parseClaimsJws(token);

        // Get userId from payload/body
        Long userId = jwtClaims.getBody()
                .get("id", Long.class);

        // Get user from database
        UserEntity userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        // Build principalDto
        return UserConverter.fromUserEntityToPrincipalDto(userEntity);
    }

    /**
     * @see AuthService#requestResetPassword(String)
     */
    @Override
    public void requestResetPassword(String email) {
        String subject = "Request to reset password";
        UserEntity user = getUserByEmail(email);
        String token = generateTokenForValidations();
        Date expiresAt = new Date(new Date().getTime() +
                Duration.ofHours(this.expiresInHours).toMillis());

        user.setResetPasswordToken(token);
        user.setResetPasswordExpireToken(expiresAt);

        LOGGER.debug("Persisting reset password token on database");
        try {
            this.userRepository.save(user);

        } catch (Exception e) {
            LOGGER.error("Failed while saving token into database", e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        this.emailServiceImp.sendEmail(
                EmailDto.builder()
                        .name(user.getFirstname())
                        .email(user.getEmail())
                        .resetPasswordToken(token)
                        .expireTimePasswordToken(String.valueOf(this.expiresInHours))
                        .build(),
                EmailTemplate.RESET_PASSWORD,
                subject
        );
    }

    /**
     * @see AuthService#validateResetPassToken(String)
     */
    @Override
    public ResetPasswordTokenDto validateResetPassToken(String token) {
        UserEntity userEntity = getUserFromResetPasswordToken(token);

        return ResetPasswordTokenDto.builder()
                .token(token)
                .name(userEntity.getFirstname())
                .userId(userEntity.getUserId())
                .build();
    }

    /**
     * @see AuthService#resetPassword(String, ResetPasswordDto)
     */
    @Override
    public void resetPassword(String token, ResetPasswordDto resetPasswordDto) {
        String subject = "Confirm of Password Reset";
        UserEntity userEntity = getUserFromResetPasswordToken(token);

        String encryptedPassword = passwordEncoder.encode(resetPasswordDto.getPassword());
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setResetPasswordToken(null);
        userEntity.setResetPasswordExpireToken(new Date());

        LOGGER.debug("Persisting reset password token as null on database");
        try {
            this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving token as null into database", e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        this.emailServiceImp.sendEmail(
                EmailDto.builder()
                        .name(userEntity.getFirstname())
                        .email(userEntity.getEmail())
                        .build(),
                EmailTemplate.CHANGE_PASSWORD,
                subject
        );
    }

    /**
     * @see AuthService#confirmEmail(String)
     */
    @Override
    public void confirmEmail(String token) {
        UserEntity userEntity = getUserFromConfirmEmailToken(token);
        userEntity.setConfirmEmailToken(null);
        userEntity.setConfirmEmailExpireToken(new Date());
        userEntity.setEmailConfirmed(true);

        LOGGER.debug("Persisting email confirmation on database");
        try {
            this.userRepository.save(userEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving email confirmation into database", e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }
    }

    /**
     * Helper to create JWT Token
     *
     * @param principalDto principal dto
     * @return the token as {@link String}
     */
    protected String generateJwtToken(PrincipalDto principalDto) {
        Date now = new Date(System.currentTimeMillis());
        Date expiresAt = new Date(now.getTime() +
                Duration.ofDays(this.jwtProperties.getExpiresInDays()).toMillis());

        return Jwts.builder()
                .setIssuedAt(now)
                .claim("id", principalDto.getUserId())
                .claim("name", principalDto.getName())
                .claim("role", principalDto.getRoles())
                .signWith(Keys.hmacShaKeyFor(this.signingKey.getEncoded()))
                .setExpiration(expiresAt)
                .compact();
    }

    /**
     * Generate token for password reset and email confirmation
     *
     * @return token
     */
    protected String generateTokenForValidations() {
        String token = UUID.randomUUID().toString().toLowerCase();
        token = token.replaceAll("-", "");
        return token;
    }

    /**
     * Get user by reset password token if the token is after the actual date
     *
     * @param token token
     * @return {@link UserEntity}
     */
    private UserEntity getUserFromResetPasswordToken(String token) {
        LOGGER.debug("Verifying if the token {} exists or is expired", token);
        return this.userRepository.findByResetPasswordTokenAndResetPasswordExpireTokenIsAfter(
                token, new Date()
        ).orElseThrow(() -> {
            LOGGER.error("The token is invalid or it expired already");
            return new InvalidTokenException(ErrorMessages.INVALID_TOKEN);
        });
    }

    /**
     * Get user by confirm email token if the token is after the actual date
     *
     * @param token token
     * @return {@link UserEntity}
     */
    private UserEntity getUserFromConfirmEmailToken(String token) {
        LOGGER.debug("Verifying if the token {} exists or is expired", token);
        return this.userRepository.findByConfirmEmailTokenAndConfirmEmailExpireTokenIsAfter(
                token, new Date()
        ).orElseThrow(() -> {
            LOGGER.error("The token is invalid or it expired already");
            return new InvalidTokenException(ErrorMessages.INVALID_TOKEN);
        });
    }

    /**
     * Get User by email
     *
     * @param email email
     * @return {@link UserEntity}
     */
    private UserEntity getUserByEmail(String email) {
        LOGGER.debug("Verifying if user with email {} exists in database", email);
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.error("The user with email {} does not exist in database", email);
                    return new UserNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });
    }
}
