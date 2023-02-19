package com.ticket.shop.service;

import com.ticket.shop.command.email.EmailDto;
import com.ticket.shop.enumerators.EmailTemplate;
import com.ticket.shop.gateway.EmailGateway;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link EmailServiceImp} implementation
 */
@Service
public class EmailServiceImp implements EmailService {

    private final EmailGateway emailGateway;

    public EmailServiceImp(EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    /**
     * @see EmailService#sendEmail(EmailDto, EmailTemplate, String)
     */
    @Override
    public void sendEmail(EmailDto emailDto, EmailTemplate template, String subject) {
        //TODO Value to be updated when a frontend app exists
        String baseAppUrl = "fe-base-url";

        String[] names = emailDto.getName().split(" ");

        Map<String, String> templateData = new HashMap<>();
        templateData.put("name", names[0]);
        templateData.put("loginURL", baseAppUrl + "/login");
        templateData.put("resetPassURL", baseAppUrl + "/reset-password?t=" + emailDto.getResetPasswordToken());
        templateData.put("email", emailDto.getEmail());
        templateData.put("expireTimeToken", emailDto.getExpireTimeToken());

        this.emailGateway.sendEmail(
                emailDto.getEmail(),
                templateData,
                template,
                subject
        );
    }
}
