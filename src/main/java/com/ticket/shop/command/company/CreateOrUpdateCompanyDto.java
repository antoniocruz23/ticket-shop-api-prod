package com.ticket.shop.command.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * CreateCompanyDto used to store company info when created
 */
@Data
@Builder
public class CreateOrUpdateCompanyDto {

    @Schema(example = "Company")
    @NotBlank(message = "Must have a name")
    private String name;

    @Schema(example = "company@email.com")
    @NotBlank(message = "Must have a email")
    private String email;

    @Schema(example = "company.com")
    @NotBlank(message = "Must have a website")
    private String website;
}
