package com.ticket.shop.command.worker;

import com.ticket.shop.enumerators.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * UpdateWorkerDto used to update worker info
 */
@Data
@Builder
public class UpdateWorkerDto {

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

    @Schema(example = "[\"Worker\"]")
    @NotNull(message = "Must have role")
    private Set<UserRole> roles;

    @Schema(example = "1")
    @NotNull(message = "Must have a country")
    private Long countryId;

    /**
     * Override to String to avoid show the password
     * in the logs if printing the entire object
     */
    @Override
    public String toString() {
        return "UpdateWorkerDto{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", role='" + roles.stream().map(r -> r.name() + " | ") + '\'' +
                ", password='***'" +
                '}';
    }
}
