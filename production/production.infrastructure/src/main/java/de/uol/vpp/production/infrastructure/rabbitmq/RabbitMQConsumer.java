package de.uol.vpp.production.infrastructure.rabbitmq;

import de.uol.vpp.production.infrastructure.scheduler.ProductionScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final ProductionScheduler productionScheduler;

    @RabbitListener(queues = "${vpp.rabbitmq.queue.action.to.production}")
    public void receivedActionRequest(ActionRequestMessage message) {
        log.info("receivedActionRequest: {}, {}", message.getActionRequestId(), message.getVppId());
        log.info("Start to generate productions...");
        productionScheduler.createProduction(message.getActionRequestId(), message.getVppId());
    }


}