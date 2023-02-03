package com.ticket.shop.command.user;

import lombok.Builder;
import lombok.Data;

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
    private Long country_id;
    private Long company_id;
}
