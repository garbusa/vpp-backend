package de.uol.vpp.action.domain.services;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.exceptions.ActionServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für das Service der Maßnahmenabfrage in der Serviceschicht
 */
public interface IActionRequestService {
    /**
     * Hole alle Maßnahmenabfragen
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste von Maßnahmenabfragen Domänen-Entities
     * @throws ActionServiceException e
     */
    List<ActionRequestAggregate> getAllActionRequestByVppId(String virtualPowerPlantId) throws ActionServiceException;

    /**
     * Hole spezifische Maßnahmenabfrage
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Maßnahmenabfrage
     * @throws ActionServiceException e
     */
    ActionRequestAggregate get(String actionRequestId) throws ActionServiceException;

    /**
     * Speichert Maßnahmenabfrage (nur wenn Stammdaten-Service aktiv)
     * Führt Geschäftslogik aus, wie die Überprüfung von Manipulationsüberlappungen und stößt
     * das Ablegen der Nachricht in die RabbitMQ, wenn alles in Ordnung ist.
     * <p>
     * Ab hier ist eine Maßnahmenabfrage "STARTED"
     *
     * @param domainEntity Maßnahmenabfrage
     * @throws ActionServiceException e
     */
    void save(ActionRequestAggregate actionRequestAggregate) throws ActionServiceException;
}
