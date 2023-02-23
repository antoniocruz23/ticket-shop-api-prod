package com.ticket.shop.service;

import com.ticket.shop.command.email.EmailDto;
import com.ticket.shop.enumerators.EmailTemplate;

/**
 * Common interface for sending emails
 */
public interface EmailService {

    /**
     * Send Email
     *
     * @param emailDto      {@link EmailDto}
     * @param emailTemplate {@link EmailTemplate}
     * @param subject       subject
     */
    void sendEmail(EmailDto emailDto, EmailTemplate emailTemplate, String subject);

    /**
     * Send Email to confirm email address
     *
     * @param firstName      first name
     * @param email          email
     * @param token          token
     * @param expiresInHours token expires in hours
     */
    void sendEmailToConfirmEmailAddress(String firstName, String email, String token, Long expiresInHours);
}
