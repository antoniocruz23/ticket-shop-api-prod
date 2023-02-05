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
    private String secretKey = "7f928de2afee05bce432f166d140c04a08f713c7eafd05bce432";

    public AuthServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    protected void init() {
        this.secretKey = Base64.getEncoder().encodeToString(this.jwtProperties.getSecretKey().getBytes());
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
        // TODO: Replace deprecated method
        Jws<Claims> jwtClaims = Jwts.parser()
                .setSigningKey(this.secretKey)
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
     * @param principalDto
     * @return the token as {@link String}
     */
    private String generateJwtToken(PrincipalDto principalDto) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date(System.currentTimeMillis());
        Date expiresAt = new Date(now.getTime() +
                Duration.ofDays(this.jwtProperties.getExpiresInDays()).toMillis());

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(this.secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setIssuedAt(now)
                .claim("id", principalDto.getUserId())
                .claim("name", principalDto.getName())
                .claim("role", principalDto.getRoles())
                .signWith(Keys.hmacShaKeyFor(signingKey.getEncoded()))
                .setExpiration(expiresAt)
                .compact();
    }
}
