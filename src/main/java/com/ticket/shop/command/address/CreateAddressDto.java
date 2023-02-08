package com.ticket.shop.command.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CreateAddressDto used to store address info when created
 */
@Data
@Builder
public class CreateAddressDto {

    @Schema(example = "Street example")
    @NotBlank(message = "Must have line1")
    private String line1;

    @Schema(example = "line2 example")
    private String line2;

    @Schema(example = "line3 example")
    private String line3;

    @Schema(example = "91342142")
    private String mobileNumber;

    @Schema(example = "CV11AA")
    @NotBlank(message = "Must have post code")
    private String postCode;

    @Schema(example = "New york")
    @NotBlank(message = "Must have a city")
    private String city;

    @Schema(example = "1")
    @NotNull(message = "Must have a country id")
    private Long countryId;
}
