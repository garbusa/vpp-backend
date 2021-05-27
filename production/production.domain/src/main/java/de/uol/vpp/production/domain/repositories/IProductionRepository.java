package de.uol.vpp.production.domain.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.exceptions.ProductionRepositoryException;
import de.uol.vpp.production.domain.valueobjects.ProductionActionRequestIdVO;

import java.util.List;

/**
 * Schnittstellendefinition für das Erzeugungsaggregat Repository in der Infrastrukturschicht
 */
public interface IProductionRepository {
    /**
     * Holt alle Erzeugungsaggregate (Tagesprognose) einer Maßnahmenabfrage
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Liste aller Erzeugungsaggregate (Tagesprognose)
     * @throws ProductionRepositoryException e
     */
    List<ProductionAggregate> getProductions(ProductionActionRequestIdVO actionRequestId) throws ProductionRepositoryException;

    /**
     * Persistiert Erzeugungsaggregat
     *
     * @param production Erzeugungsaggregat
     * @throws ProductionRepositoryException e
     */
    void saveProduction(ProductionAggregate production) throws ProductionRepositoryException;
}
