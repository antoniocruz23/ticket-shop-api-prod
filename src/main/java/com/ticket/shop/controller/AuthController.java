package com.ticket.shop.controller;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.RecoverPasswordDto;
import com.ticket.shop.command.auth.ResetPasswordDto;
import com.ticket.shop.command.auth.ResetPasswordTokenDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ticket.shop.security.CookieAuthFilter.COOKIE_NAME;

/**
 * REST controller responsible for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth endpoints")
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login user with email and password
     *
     * @param credentials user credentials
     * @return {@link LoggedInDto} with user info and jwt token
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = LoggedInDto.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.WRONG_CREDENTIALS,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<LoggedInDto> login(@RequestBody CredentialsDto credentials) {

        LOGGER.info("Request to login user with email {}", credentials.getEmail());
        LoggedInDto loggedIn;
        try {
            loggedIn = this.authService.loginUser(credentials);

            ResponseCookie cookie = ResponseCookie
                    .from(COOKIE_NAME, loggedIn.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(24 * 60 * 60)
                    .path("/")
                    .build();

            LOGGER.info("User logged in successfully. Retrieving jwt token and setting cookie");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(loggedIn);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to logging user - {}", credentials, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
    }

    /**
     * Recover Password
     * An email will be sent with the link to make the reset
     *
     * @param recoverPassword {@link RecoverPasswordDto}
     */
    @PutMapping("/reset-password")
    @Operation(summary = "Recover password", description = "Request to recover password")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No Content")})
    public ResponseEntity<Void> recoverPassword(@RequestBody RecoverPasswordDto recoverPassword) {

        try {
            this.authService.requestRecoverPassword(recoverPassword.getEmail());

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed while creating the request to recover password of email - {}", recoverPassword, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Verify if the token is still valid
     * to make the password reset
     *
     * @param token token
     * @return {@link ResetPasswordTokenDto}
     */
    @GetMapping("/reset-password/verify-token")
    @Operation(summary = "Validate token to reset password", description = "Request to validate token to reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(schema = @Schema(implementation = ResetPasswordTokenDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(schema = @Schema(implementation = ResetPasswordTokenDto.class)))})
    public ResponseEntity<ResetPasswordTokenDto> validateResetPasswordToken(@RequestParam String token) {

        ResetPasswordTokenDto resetPasswordTokenDto;
        try {
            resetPasswordTokenDto = this.authService.validateResetPasswordToken(token);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed while verifying reset password token - {}", token, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        return new ResponseEntity<>(resetPasswordTokenDto, HttpStatus.OK);
    }

    /**
     * Reset Password with given token
     *
     * @param token            token
     * @param resetPasswordDto {@link ResetPasswordDto}
     */
    @PutMapping("/reset-password/complete")
    @Operation(summary = "Reset password with token", description = "Request to reset password with token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    public ResponseEntity<Void> resetPassword(@RequestParam String token,
                                              @RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            this.authService.resetPassword(token, resetPasswordDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed while saving new user password with token - {}", token, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Confirm email address with token
     *
     * @param token token
     */
    @PutMapping("/confirm-email")
    @Operation(summary = "Confirm email", description = "Confirm email with token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    public ResponseEntity<Void> confirmEmail(@RequestParam String token) {

        try {
            this.authService.confirmEmail(token);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed while confirming email with token - {}", token, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        return ResponseEntity.noContent().build();
    }
}
