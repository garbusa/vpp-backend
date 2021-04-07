package de.uol.vpp.load.infrastructure.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = "${vpp.rabbitmq.queue}")
    public void recievedMessage(String message) {
        System.out.println("Recieved Message From RabbitMQ: " + message);
    }
}