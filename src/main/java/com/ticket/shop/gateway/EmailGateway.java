package com.ticket.shop.gateway;

import com.ticket.shop.enumerators.EmailTemplate;

import java.util.Map;

/**
 * Email Gateway
 */
public interface EmailGateway {
    void sendEmail(String email, Map<String, String> templateData, EmailTemplate template, String subject);
}
