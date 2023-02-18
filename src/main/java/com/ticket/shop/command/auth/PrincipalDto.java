package com.ticket.shop.command.auth;

import com.ticket.shop.enumerators.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Principal information
 * principal definition - entity who can authenticate (user, other service, third-parties...)
 */
@Data
@Builder
public class PrincipalDto {
    private Long userId;
    private String name;
    private String email;
    private List<UserRole> roles;
    private Long countryId;
    private Long companyId;
}
