package de.uol.vpp.load.domain.services;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadServiceException;

import java.util.List;

public interface ILoadService {
    List<LoadAggregate> getLoadsByActionRequestId(String actionRequestBusinessKey) throws LoadServiceException;
}
