package com.ticket.shop.command.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reset password token dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordTokenDto {
    private String token;
    private String name;
    private Long userId;
}
