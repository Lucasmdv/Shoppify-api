package org.stockify.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.stockify.config.GlobalPreferencesConfig;

/**
 * Service responsible for sending email notifications.
 */
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final GlobalPreferencesConfig globalPreferencesConfig;

    /**
     * Sends an email with the specified subject and body to the configured recipient.
     *
     * @param subject     email subject
     * @param description email body
     */
    public void sendEmail(String subject, String description) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(globalPreferencesConfig.getEmailAddress());
        message.setSubject(subject);
        message.setText(description);
        mailSender.send(message);
    }
}
