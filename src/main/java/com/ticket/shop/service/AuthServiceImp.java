package com.ticket.shop.service;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.converter.UserConverter;
import com.ticket.shop.error.ErrorMessages;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

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

    public AuthServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
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
}
