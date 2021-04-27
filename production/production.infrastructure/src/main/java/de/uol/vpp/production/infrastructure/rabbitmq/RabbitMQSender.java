package de.uol.vpp.production.infrastructure.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RabbitMQSender {

    private final AmqpTemplate rabbitTemplate;

    @Value("${vpp.rabbitmq.exchange.forecastGeneration}")
    private String forecastGenerationExchange;

    @Value("${vpp.rabbitmq.key.production.to.action}")
    private String productionToActionKey;


    public void send(String actionRequestId, Long timestamp) {
        ProductionMessage productionMessage = new ProductionMessage();
        productionMessage.setActionRequestId(actionRequestId);
        productionMessage.setTimestamp(timestamp);
        rabbitTemplate.convertAndSend(forecastGenerationExchange, productionToActionKey, productionMessage);
        log.info("Send productionMessage: {}, {}", productionMessage.getActionRequestId(), productionMessage.getTimestamp());
    }
}
