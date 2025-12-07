package com.example.fms.booking.messaging;

import com.example.fms.common.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.fms.booking.config.RabbitMQConfig.EXCHANGE;
import static com.example.fms.booking.config.RabbitMQConfig.ROUTING_KEY;

@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmail(EmailEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}
