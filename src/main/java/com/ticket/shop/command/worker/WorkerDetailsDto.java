package com.ticket.shop.command.worker;

import com.ticket.shop.enumerators.UserRoles;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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
    private List<UserRoles> roles;
    private Long countryId;
    private Long companyId;
}
