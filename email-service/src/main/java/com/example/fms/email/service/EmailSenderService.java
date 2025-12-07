package com.example.fms.email.service;

import com.example.fms.common.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUser;

    public void sendEmail(EmailEvent event) {

        log.debug("EMAIL SERVICE → Using SMTP user: {}", mailUser);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.to());
            message.setSubject(event.subject());
            message.setText(event.body());

            log.debug("Attempting to send email to: {}", event.to());

            mailSender.send(message);

            log.info("Email sent successfully to {}", event.to());

        } catch (Exception ex) {
            log.error("EMAIL SENDING FAILED");
            log.error("To: {}", event.to());
            log.error("SMTP User: {}", mailUser);
            log.error("Reason: {}", ex.getMessage());
        }
    }
}
