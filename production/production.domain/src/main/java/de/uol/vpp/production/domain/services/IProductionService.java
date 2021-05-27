package de.uol.vpp.production.domain.services;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.exceptions.ProductionServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für das Erzeugungsaggregat Service in der Serviceschicht
 */
public interface IProductionService {
    /**
     * Holt alle Erzeugungsaggregate für eine Maßnahmenabfrage
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Liste von Erzeugungsaggregaten (Tagesprognose)
     * @throws ProductionServiceException e
     */
    List<ProductionAggregate> getProductionsByActionRequestId(String actionRequestId) throws ProductionServiceException;
}
