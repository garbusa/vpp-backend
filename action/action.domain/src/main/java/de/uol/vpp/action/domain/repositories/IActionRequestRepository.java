package de.uol.vpp.action.domain.repositories;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestVirtualPowerPlantIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das Repository der Maßnahmenabfrage in der Infrastrukturschicht
 */
public interface IActionRequestRepository {
    /**
     * Holt alle Maßnahmenabfrage durch VK Id
     *
     * @param virtualPowerPlantId Id des VK
     * @return Maßnahmenabfragen
     * @throws ActionRepositoryException e
     */
    List<ActionRequestAggregate> getAllActionRequestsByVppId(ActionRequestVirtualPowerPlantIdVO virtualPowerPlantId) throws ActionRepositoryException;


    /**
     * Holt spezifische Maßnahmenabfrage
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Maßnahmenabfrage
     * @throws ActionRepositoryException e
     */
    Optional<ActionRequestAggregate> getActionRequest(ActionRequestIdVO actionRequestId) throws ActionRepositoryException;

    /**
     * Speichert Maßnahmenabfrage ab und unterscheided ob es eine initiale Speicherung oder
     * eine Aktualisierung ist.
     *
     * @param actionRequest Maßnahmenabfrage
     * @param isInitialSave ist initiale Speicherung?
     * @throws ActionRepositoryException e
     */
    void saveActionRequest(ActionRequestAggregate actionRequest, boolean isInitialSave) throws ActionRepositoryException;
}
