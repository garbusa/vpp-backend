package de.uol.vpp.load.infrastructure.rabbitmq;

import de.uol.vpp.load.infrastructure.rabbitmq.messages.ActionRequestMessage;
import de.uol.vpp.load.infrastructure.scheduler.LoadScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final LoadScheduler loadScheduler;

    @RabbitListener(queues = "${vpp.rabbitmq.queue.action.to.load}")
    public void receivedActionRequest(ActionRequestMessage message) {
        log.info("receivedActionRequest: {}, {}", message.getActionRequestId(), message.getVppId());
        log.info("Start to generate loads...");
        loadScheduler.createLoad(message.getActionRequestId(), message.getVppId());
    }


}