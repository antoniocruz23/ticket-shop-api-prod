package com.ticket.shop.command.event;

import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.command.price.CreatePriceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * CreateEventDto used to store event info when created
 */
@Data
@Builder
public class CreateEventDto {

    @Schema(example = "Festival")
    @NotBlank(message = "Must have name")
    private String name;

    @Schema(example = "Street example")
    @NotBlank(message = "Must have line1")
    private String description;

    @Valid
    @NotNull(message = "Must have an address")
    private CreateAddressDto address;

    @Valid
    @NotNull(message = "Must have the prices")
    private List<CreatePriceDto> prices;
}
