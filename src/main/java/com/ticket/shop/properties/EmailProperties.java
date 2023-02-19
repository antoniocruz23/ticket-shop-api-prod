package com.ticket.shop.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Email templates
 */
@Data
@Component
@ConfigurationProperties(prefix = "platform.email")
public class EmailProperties {
    private Map<String, String> sendgridTemplates;
}
