package com.ticket.shop.service;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.command.auth.ResetPasswordDto;
import com.ticket.shop.command.auth.ResetPasswordTokenDto;

/**
 * Common interface for authorization operations
 */
public interface AuthService {

    /**
     * Login user
     *
     * @param credentialsDto {@link CredentialsDto}
     * @return {@link LoggedInDto} logged in user details
     */
    LoggedInDto loginUser(CredentialsDto credentialsDto);

    /**
     * Validate token
     *
     * @param token token
     * @return {@link PrincipalDto} principal authenticated
     */
    PrincipalDto validateToken(String token);

    /**
     * Request to recover password
     *
     * @param email email
     */
    void requestRecoverPassword(String email);

    /**
     * Reset password with token
     *
     * @param token            token
     * @param resetPasswordDto {@link ResetPasswordDto}
     */
    void resetPassword(String token, ResetPasswordDto resetPasswordDto);

    /**
     * Validate reset password token
     *
     * @param token token
     * @return {@link ResetPasswordTokenDto}
     */
    ResetPasswordTokenDto validateResetPasswordToken(String token);

    /**
     * Confirm email with token
     *
     * @param token token
     */
    void confirmEmail(String token);
}
