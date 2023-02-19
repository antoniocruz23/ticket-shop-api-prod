package com.ticket.shop.command.email;

import lombok.Builder;
import lombok.Data;

/**
 * Email Dto
 */
@Data
@Builder
public class EmailDto {
    private String name;
    private String email;
    private String password;
    private String resetPasswordToken;
    private String expireTimeToken;
}
