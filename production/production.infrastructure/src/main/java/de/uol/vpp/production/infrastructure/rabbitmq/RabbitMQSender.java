package de.uol.vpp.production.infrastructure.rabbitmq;

import de.uol.vpp.production.infrastructure.rabbitmq.messages.ActionFailedMessage;
import de.uol.vpp.production.infrastructure.rabbitmq.messages.ProductionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ Sender
 * Legt Nachrichten für den Maßnahmen-Service bereit, sobald die Erzeugungsprognose
 * erfolgreich beendet wurde oder ein Fehler aufgetreten ist
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

    @Value("${vpp.rabbitmq.key.production.to.action}")
    private String productionToActionKey;

    @Value("${vpp.rabbitmq.key.load.to.action.failed}")
    private String loadToActionFailedKey;


    /**
     * Sendet Nachricht an den Maßnahmen-Service, wenn Erzeugungsprognose vollendet ist
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @param timestamp       aktueller Zeitstempel
     */
    public void send(String actionRequestId, Long timestamp) {
        ProductionMessage productionMessage = new ProductionMessage();
        productionMessage.setActionRequestId(actionRequestId);
        productionMessage.setTimestamp(timestamp);
        rabbitTemplate.convertAndSend(forecastGenerationExchange, productionToActionKey, productionMessage);
        log.info("Die Erzeugungsprognosen wurden erstellt und der Maßnahmenservice wird benachrichtigt: Maßnahmenabfrage {}, Zeitstempel {}", productionMessage.getActionRequestId(), productionMessage.getTimestamp());
    }

    /**
     * Sendet Nachricht an den Maßnahmen-Service, wenn Erzeugungsprognose fehlerhaft ist
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     */
    public void sendFailed(String actionRequestId) {
        ActionFailedMessage failedMessage = new ActionFailedMessage();
        failedMessage.setActionRequestId(actionRequestId);
        rabbitTemplate.convertAndSend(actionRequestFailedExchange, loadToActionFailedKey, failedMessage);
        log.info("Die Erstellung der Erzeugungsprognosen ist fehlgeschlagen: Maßnahmenabfrage {}", failedMessage.getActionRequestId());
    }
}
