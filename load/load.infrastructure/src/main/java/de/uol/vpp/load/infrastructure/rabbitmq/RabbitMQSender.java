package de.uol.vpp.load.infrastructure.rabbitmq;

import de.uol.vpp.load.infrastructure.rabbitmq.messages.ActionFailedMessage;
import de.uol.vpp.load.infrastructure.rabbitmq.messages.LoadMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ Sender
 * Benachrichtigt Maßnahmen-Service, wenn die Lastgenerierung erfolgreich beendet ist oder ein Fehler aufgetreten ist.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class RabbitMQSender {

    private final AmqpTemplate rabbitTemplate;

    @Value("${vpp.rabbitmq.exchange.forecastGeneration}")
    private String forecastGenerationExchange;

    @Value("${vpp.rabbitmq.exchange.actionRequestFailed}")
    private String actionRequestFailedExchange;

    @Value("${vpp.rabbitmq.key.load.to.action}")
    private String loadToActionKey;

    @Value("${vpp.rabbitmq.key.load.to.action.failed}")
    private String loadToActionFailedKey;

    /**
     * Benachrichtigt Maßnahmen-Service, dass die Lastprognose erstellt ist
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @param timestamp       Zeitstempel
     */
    public void send(String actionRequestId, Long timestamp) {
        LoadMessage loadMessage = new LoadMessage();
        loadMessage.setActionRequestId(actionRequestId);
        loadMessage.setTimestamp(timestamp);
        rabbitTemplate.convertAndSend(forecastGenerationExchange, loadToActionKey, loadMessage);
        log.info("Send loadMessage: {}, {}", loadMessage.getActionRequestId(), loadMessage.getTimestamp());
    }

    /**
     * Benachrichtigt Maßnahmen-Service, dass ein Fehler in der Generierung der Lastprognose aufgetreten ist.
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     */
    public void sendFailed(String actionRequestId) {
        ActionFailedMessage failedMessage = new ActionFailedMessage();
        failedMessage.setActionRequestId(actionRequestId);
        rabbitTemplate.convertAndSend(actionRequestFailedExchange, loadToActionFailedKey, failedMessage);
        log.info("Send failedMessage: {}", failedMessage.getActionRequestId());
    }
}
