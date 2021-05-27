package de.uol.vpp.production.domain.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionProducerRepositoryException;

/**
 * Schnittstellendefinition für das Erzeugungswert Repository in der Infrastrukturschicht
 */
public interface IProductionProducerRepository {
    /**
     * Weist Erzeugungswert dem Erzeugungsaggregat mittels internet Datenbank-Id
     *
     * @param producerInternalId interne Datenbank Id
     * @param production         Erzeugungsaggregat
     * @throws ProductionProducerRepositoryException e
     */
    void assignToInternal(Long producerInternalId, ProductionAggregate production) throws ProductionProducerRepositoryException;

    /**
     * Persistiert Erzeugungswert
     *
     * @param productionProducer Erzeugungswert Entität
     * @throws ProductionProducerRepositoryException e
     */
    Long saveProductionProducerInternal(ProductionProducerEntity productionProducer) throws ProductionProducerRepositoryException;

}
