package com.ticket.shop.command.user;

import com.ticket.shop.enumerators.UserRoles;
import lombok.Builder;
import lombok.Data;

/**
 * UserDetailDto used to respond with user details
 */
@Data
@Builder
public class UserDetailsDto {
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private UserRoles roles;
}
