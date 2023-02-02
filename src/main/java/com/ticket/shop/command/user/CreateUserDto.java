package com.ticket.shop.command.user;

import com.ticket.shop.enumerators.UserRoles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * CreateUserDto used to store user info when created
 */
@Data
@Builder
public class CreateUserDto {

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

    private List<UserRoles> roles;

    @NotNull(message = "Must have a country")
    private Long countryId;

    /**
     * Override to String to avoid show the password
     * in the logs if printing the entire object
     */
    @Override
    public String toString() {
        return "CreateUserDto{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='***'" +
                '}';
    }
}
