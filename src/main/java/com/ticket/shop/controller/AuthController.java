package com.ticket.shop.controller;

import com.ticket.shop.command.auth.CredentialsDto;
import com.ticket.shop.command.auth.LoggedInDto;
import com.ticket.shop.command.auth.RequestResetPasswordDto;
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

    @PutMapping("/reset-password")
    @Operation(summary = "Recovery password", description = "Request to recovery password")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No Content")})
    public ResponseEntity<Void> requestResetPassword(@RequestBody RequestResetPasswordDto resetPassword) {

        this.authService.requestResetPassword(resetPassword.getEmail());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reset-password/verify-token")
    @Operation(summary = "Validate Recovery password", description = "Request to validate token of recovery password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(schema = @Schema(implementation = ResetPasswordTokenDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(schema = @Schema(implementation = ResetPasswordTokenDto.class)))})
    public ResponseEntity<ResetPasswordTokenDto> validateResetPassToken(@RequestParam("token") String token) {

        ResetPasswordTokenDto resetPasswordTokenDto = this.authService.validateResetPassToken(token);

        return ResponseEntity.ok(resetPasswordTokenDto);
    }

    @PutMapping("/reset-password/complete")
    @Operation(summary = "Reset password with token", description = "Request to reset password with token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    public ResponseEntity<Void> resetPassword(@RequestParam("token") String token,
                                              @RequestBody ResetPasswordDto resetPasswordDto) {

        this.authService.resetPassword(token, resetPasswordDto);

        return ResponseEntity.noContent().build();
    }
}
