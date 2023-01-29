package com.ticket.shop.service;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.PrincipalDto;

/**
 * Common interface for authorization operations
 */
public interface AuthService {

    /**
     * Login user
     * @param credentialsDto
     * @return {@link LoggedInDto} logged in user details
     */
    LoggedInDto loginUser(CredentialsDto credentialsDto);

    /**
     * Validate token
     * @param token
     * @return {@link PrincipalDto} principal authenticated
     */
    PrincipalDto validateToken(String token);
}
