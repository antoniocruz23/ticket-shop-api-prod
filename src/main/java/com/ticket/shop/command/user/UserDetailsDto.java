package com.ticket.shop.command.user;

import lombok.Builder;
import lombok.Data;

/**
 * UserDetailDto used to respond with user details
 */
@Data
@Builder
public class UserDetailsDto {
    private Long userId;
    private String firstname;
    private String lastname;
    private String email;
}
