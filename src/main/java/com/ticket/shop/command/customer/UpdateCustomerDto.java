package com.ticket.shop.command.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * UpdateCustomerDto used to update customer info
 */
@Data
@Builder
public class UpdateCustomerDto {

    @Schema(example = "New")
    @NotBlank(message = "Must have firstname")
    private String firstname;

    @Schema(example = "Name")
    @NotBlank(message = "Must have lastname")
    private String lastname;

    @Schema(example = "user.new@new.com")
    @NotBlank(message = "Must have email")
    private String email;

    @Schema(example = "87654321!")
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
        return "UpdateCustomerDto{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='***'" +
                '}';
    }
}
