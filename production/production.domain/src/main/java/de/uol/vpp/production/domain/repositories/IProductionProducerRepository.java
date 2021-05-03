package de.uol.vpp.production.domain.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionProducerRepositoryException;
import de.uol.vpp.production.domain.valueobjects.ProductionStartTimestampVO;
import de.uol.vpp.production.domain.valueobjects.ProductionVirtualPowerPlantIdVO;

import java.util.List;

public interface IProductionProducerRepository {
    List<ProductionProducerEntity> getProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO virtualPowerPlantId, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException;

    void assignToInternal(Long producerInternalId, ProductionAggregate production) throws ProductionProducerRepositoryException;

    Long saveProductionProducerInternal(ProductionProducerEntity load) throws ProductionProducerRepositoryException;

    void deleteProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO virtualPowerPlantId, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException;
}
