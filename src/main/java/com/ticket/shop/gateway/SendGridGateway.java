package com.ticket.shop.gateway;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.ticket.shop.enumerators.EmailTemplate;
import com.ticket.shop.properties.EmailProperties;
import com.ticket.shop.properties.SendGridProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * SendGrid Gateway
 */
@Component
public class SendGridGateway implements EmailGateway {

    private static final Logger LOGGER = LogManager.getLogger(SendGridGateway.class);

    private static final String FROM_EMAIL = "ticket.shop@sapo.pt";

    private final SendGrid sendGrid;
    private final SendGridProperties sendGridProperties;
    private final EmailProperties emailProperties;

    public SendGridGateway(SendGrid sendGrid, SendGridProperties sendGridProperties, EmailProperties emailProperties) {
        this.sendGrid = sendGrid;
        this.sendGridProperties = sendGridProperties;
        this.emailProperties = emailProperties;
    }

    @Async
    @Override
    public void sendEmail(String email, Map<String, String> templateData, EmailTemplate template, String subject) {

        if (!this.sendGridProperties.isEnabled()) {
            return;
        }

        Email from = new Email(FROM_EMAIL);
        Email toEmail = new Email(email);

        Personalization personalization = new Personalization();
        templateData.forEach(personalization::addDynamicTemplateData);
        personalization.addTo(toEmail);

        Mail mail = new Mail();
        mail.setTemplateId(this.emailProperties.getSendgridTemplates().get(template.getName()));
        mail.setFrom(from);
        mail.addPersonalization(personalization);
        mail.setSubject(subject);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            this.sendGrid.api(request);
            LOGGER.info(String.format("Email %s sent successfully", template.getName()));

        } catch (IOException ex) {
            LOGGER.error(String.format("Failed to send email %s to %s", template.getName(), email));
            ex.printStackTrace();
        }
    }
}
