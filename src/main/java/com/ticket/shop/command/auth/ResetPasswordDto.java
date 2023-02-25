package com.ticket.shop.command.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reset password dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

    @Schema(example = "new.password")
    private String password;

    /**
     * Override to String to avoid show the password
     * in the logs if printing the entire object
     */
    @Override
    public String toString() {
        return "ResetPasswordDto{" +
                "password=***'" +  '\'' +
                '}';
    }
}
