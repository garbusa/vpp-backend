package de.uol.vpp.action.infrastructure.rabbitmq;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.repositories.IActionRequestRepository;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.infrastructure.algorithm.ActionCatalogInfrastructureService;
import de.uol.vpp.action.infrastructure.rabbitmq.messages.ActionRequestFailedMessage;
import de.uol.vpp.action.infrastructure.rabbitmq.messages.LoadMessage;
import de.uol.vpp.action.infrastructure.rabbitmq.messages.ProductionMessage;
import de.uol.vpp.action.infrastructure.rest.exceptions.LoadRestClientException;
import de.uol.vpp.action.infrastructure.rest.exceptions.MasterdataRestClientException;
import de.uol.vpp.action.infrastructure.rest.exceptions.ProductionRestClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Listener für RabbitMQ Queues, empfängt die erfolgreiche Generierung der Last- und Erzeugungswerte
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final ActionCatalogInfrastructureService actionCatalogInfrastructureService;
    private final IActionRequestRepository actionRequestRepository;
    private final Map<String, Integer> actionRequestIdCounterMap = new HashMap<>();

    @RabbitListener(queues = "${vpp.rabbitmq.queue.load.to.action.failed}")
    public void receivedActionFailedMessage(ActionRequestFailedMessage actionRequestFailedMessage) {
        log.info("Der Prozess der Maßnahmenabfrage {} ist fehlgeschlagen.", actionRequestFailedMessage.getActionRequestId());
        actionCatalogInfrastructureService.actionFailed(actionRequestFailedMessage.getActionRequestId());
    }

    @RabbitListener(queues = "${vpp.rabbitmq.queue.load.to.action}")
    public void receivedLoadMessage(LoadMessage loadMessage) {
        try {
            Optional<ActionRequestAggregate> actionRequest = actionRequestRepository.getActionRequest(new ActionRequestIdVO(loadMessage.getActionRequestId()));
            if (actionRequest.isPresent()) {
                this.incrementAndCheck(loadMessage.getActionRequestId());
            }
        } catch (ActionRepositoryException | ActionException e) {
            log.error(e);
        }
        log.info("Lastprognose erhalten: Maßnahmenabfrage {},  Zeitstempel {}", loadMessage.getActionRequestId(), loadMessage.getTimestamp());
    }

    private synchronized void incrementAndCheck(String actionRequestId) {
        if (actionRequestIdCounterMap.getOrDefault(actionRequestId, 0) == 0) {
            actionRequestIdCounterMap.put(actionRequestId, 0);
        }

        actionRequestIdCounterMap.put(actionRequestId, actionRequestIdCounterMap.get(actionRequestId) + 1);

        if (actionRequestIdCounterMap.get(actionRequestId) == 2) {
            actionRequestIdCounterMap.remove(actionRequestId);
            log.info("Die Prognosen für die Maßnahmenabfrage {} wurden entgegengenommen.", actionRequestId);
            try {
                actionCatalogInfrastructureService.createActionCatalogs(actionRequestId);
            } catch (ActionException | ActionRepositoryException | MasterdataRestClientException | LoadRestClientException | ProductionRestClientException e) {
                log.error("Bei der Erstellung der Maßnahmenabfrage ist ein Fehler aufgetreten.", e);
                actionCatalogInfrastructureService.actionFailed(actionRequestId);
            }
        } else if (actionRequestIdCounterMap.get(actionRequestId) == 1) {
            log.info("Die erste Prognose für die Maßnahmenabfrage {} wurde entgegengenommen. Zweite Prognose wird erwartet...", actionRequestId);
            new Thread(() -> {
                log.info("Das Warten für die zweite Prognose für die Maßnahmenabfrage {} hat begonnen.", actionRequestId);
                if (actionRequestIdCounterMap.get(actionRequestId) == 1) {
                    try {
                        TimeUnit.MINUTES.sleep(5);
                    } catch (InterruptedException e) {
                        log.info("Während des Wartens auf die Prognosen ist ein Fehler auftreten. (Maßnahmenabfrage {})", actionRequestId);
                        actionCatalogInfrastructureService.actionFailed(actionRequestId);
                    } finally {
                        if (actionRequestIdCounterMap.get(actionRequestId) != null) {
                            if (actionRequestIdCounterMap.get(actionRequestId) == 1) {
                                log.info("Während des Wartens auf die zweite Prognose ist ein Fehler aufgetreten. (Maßnahmenabfrage {})", actionRequestId);
                                actionRequestIdCounterMap.remove(actionRequestId);
                            }
                        }
                        log.info("Das Warten auf die Prognosen wurde beendet. (Maßnahmenabfrage {})", actionRequestId);
                    }
                }
            }).start();
        } else {
            log.info("Bei Empfangen der Prognosen ist etwas fehlgeschlagen. (Maßnahmenabfrage {})", actionRequestId);
            actionCatalogInfrastructureService.actionFailed(actionRequestId);
        }
    }

    @RabbitListener(queues = "${vpp.rabbitmq.queue.production.to.action}")
    public void receivedProductionMessage(ProductionMessage productionMessage) {
        log.info("Erzeugungsprognose erhalten: Maßnahmenabfrage {}, Zeitstempel {}", productionMessage.getActionRequestId(), productionMessage.getTimestamp());
        this.incrementAndCheck(productionMessage.getActionRequestId());
    }

}