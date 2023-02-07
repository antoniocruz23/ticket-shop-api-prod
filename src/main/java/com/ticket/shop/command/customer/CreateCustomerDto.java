package com.ticket.shop.command.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CreateCustomerDto used to store customer info when created
 */
@Data
@Builder
public class CreateCustomerDto {

    @Schema(example = "User")
    @NotBlank(message = "Must have firstname")
    private String firstname;

    @Schema(example = "Example")
    @NotBlank(message = "Must have lastname")
    private String lastname;

    @Schema(example = "user.example@example.com")
    @NotBlank(message = "Must have email")
    private String email;

    @Schema(example = "Abcdefg123!")
    @NotBlank(message = "Must have password")
    private String password;

    @Schema(example = "1")
    @NotNull(message = "Must have country")
    private Long countryId;

    /**
     * Override to String to avoid show the password
     * in the logs if printing the entire object
     */
    @Override
    public String toString() {
        return "CreateCustomerDto{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='***'" +
                '}';
    }
}
