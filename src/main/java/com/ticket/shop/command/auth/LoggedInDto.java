package com.ticket.shop.command.auth;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for authentication information
 */
@Data
@Builder
public class LoggedInDto {
    private PrincipalDto principal;
    private String token;
}
