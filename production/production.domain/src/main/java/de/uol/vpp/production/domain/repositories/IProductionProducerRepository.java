package de.uol.vpp.production.domain.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionProducerRepositoryException;
import de.uol.vpp.production.domain.valueobjects.ProductionStartTimestampVO;
import de.uol.vpp.production.domain.valueobjects.ProductionVirtualPowerPlantIdVO;

import java.util.List;

public interface IProductionProducerRepository {
    List<ProductionProducerEntity> getProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO vppBusinessKey, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException;

    void assign(Long producerInternalId, ProductionAggregate production) throws ProductionProducerRepositoryException;

    Long saveProductionProducer(ProductionProducerEntity load) throws ProductionProducerRepositoryException;

    void deleteProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO vppBusinessKey, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException;
}
