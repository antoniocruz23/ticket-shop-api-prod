package com.ticket.shop.command.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CredentialsDto {

    @Schema(example = "user@example.com")
    @NotBlank(message = "Email must be provided")
    private String email;

    @Schema(example = "passwordexample!")
    @NotBlank(message = "Password must be provided")
    private String password;

    /**
     * Override to String to avoid show the password
     * in the logs if printing the entire object
     */
    @Override
    public String toString() {
        return "CredentialsDto{" +
                "email='" + email + '\'' +
                ", password='***'" +
                '}';
    }
}
