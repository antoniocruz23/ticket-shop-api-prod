package com.ticket.shop.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Jwt properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "ticket-shop.jwt")
public class JwtProperties {
    private String secretKey;
    private Long expiresInDays;
}
