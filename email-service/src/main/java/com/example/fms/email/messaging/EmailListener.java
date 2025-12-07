package com.example.fms.email.messaging;

import com.example.fms.common.dto.EmailEvent;
import com.example.fms.email.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailListener {

    private final EmailSenderService emailSenderService;

    @RabbitListener(queues = "email.queue")
    public void handleEmailEvent(EmailEvent event) {

        log.info("Received EmailEvent from RabbitMQ: {}", event);

        try {
            emailSenderService.sendEmail(event);
        } catch (Exception ex) {
            log.error("Error while processing email event: {}", ex.getMessage());
        }
    }
}
