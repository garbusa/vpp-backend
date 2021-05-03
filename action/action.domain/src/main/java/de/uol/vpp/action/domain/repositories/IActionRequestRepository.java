package de.uol.vpp.action.domain.repositories;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestVirtualPowerPlantIdVO;

import java.util.List;
import java.util.Optional;

public interface IActionRequestRepository {
    List<ActionRequestAggregate> getAllActionRequestsByVppId(ActionRequestVirtualPowerPlantIdVO virtualPowerPlantId) throws ActionRepositoryException;

    Optional<ActionRequestAggregate> getActionRequest(ActionRequestIdVO actionRequestId) throws ActionRepositoryException;

    void saveActionRequest(ActionRequestAggregate actionRequest, boolean isInitialSave) throws ActionRepositoryException;
}
