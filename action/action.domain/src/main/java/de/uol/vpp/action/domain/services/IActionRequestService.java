package de.uol.vpp.action.domain.services;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.exceptions.ActionServiceException;

import java.util.List;

public interface IActionRequestService {
    List<ActionRequestAggregate> getAllActionRequestByVppId(String virtualPowerPlantId) throws ActionServiceException;

    ActionRequestAggregate get(String actionRequestId) throws ActionServiceException;

    void save(ActionRequestAggregate actionRequestAggregate) throws ActionServiceException;
}
