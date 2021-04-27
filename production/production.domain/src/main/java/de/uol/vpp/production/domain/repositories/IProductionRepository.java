package de.uol.vpp.production.domain.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.exceptions.ProductionRepositoryException;
import de.uol.vpp.production.domain.valueobjects.ProductionActionRequestIdVO;

import java.util.List;

public interface IProductionRepository {
    List<ProductionAggregate> getProductions(ProductionActionRequestIdVO actionRequestBusinessKey) throws ProductionRepositoryException;

    void saveProduction(ProductionAggregate production) throws ProductionRepositoryException;

    void deleteProductionsByActionRequestId(ProductionActionRequestIdVO actionRequestBusinessKey) throws ProductionRepositoryException;

    void updateProduction(ProductionAggregate production) throws ProductionRepositoryException;
}
