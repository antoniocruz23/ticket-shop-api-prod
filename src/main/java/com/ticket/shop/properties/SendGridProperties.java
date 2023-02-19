package com.ticket.shop.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SendGrid properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "platform.sendgrid")
public class SendGridProperties {
    private String apiKey;
    private boolean enabled;
}

