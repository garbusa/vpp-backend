package de.uol.vpp.action.infrastructure.rabbitmq;

import de.uol.vpp.action.infrastructure.rabbitmq.messages.ActionRequestMessage;
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

    @Value("${vpp.rabbitmq.exchange.actionRequest}")
    private String actionRequestExchange;

    @Value("${vpp.rabbitmq.key.action.to.load}")
    private String actionToLoadKey;

    @Value("${vpp.rabbitmq.key.action.to.production}")
    private String actionToProductionKey;

    public void sendActionRequest(ActionRequestMessage message) {
        rabbitTemplate.convertAndSend(actionRequestExchange, actionToLoadKey, message);
        rabbitTemplate.convertAndSend(actionRequestExchange, actionToProductionKey, message);
        log.info("sendActionRequest: {}, {}", message.getActionRequestId(), message.getVppId());
    }
}
