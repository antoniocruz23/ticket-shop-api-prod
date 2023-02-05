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

    @Schema(example = "new")
    @NotBlank(message = "Must have firstname")
    private String firstname;

    @Schema(example = "name")
    @NotBlank(message = "Must have lastname")
    private String lastname;

    @Schema(example = "user.new@new.com")
    @NotBlank(message = "Must have email")
    private String email;

    @Schema(example = "87654321!")
    @NotBlank(message = "Must have password")
    private String password;

    @NotNull(message = "Must have a country id")
    private Long countryId;
}
