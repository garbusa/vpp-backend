package de.uol.vpp.production.domain.services;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.exceptions.ProductionServiceException;

import java.util.List;

public interface IProductionService {
    List<ProductionAggregate> getProductionsByActionRequestId(String actionRequestBusinessKey) throws ProductionServiceException;
}
