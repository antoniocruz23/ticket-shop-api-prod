package com.ticket.shop.command.worker;

import com.ticket.shop.enumerators.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * WorkerDetailsDto used to respond with worker details
 */
@Data
@Builder
public class WorkerDetailsDto {
    private Long userId;
    private String firstname;
    private String lastname;
    private String email;
    private Set<UserRole> roles;
    private Long countryId;
    private Long companyId;
}
