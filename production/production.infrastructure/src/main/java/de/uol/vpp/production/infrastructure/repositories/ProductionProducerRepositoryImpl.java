package de.uol.vpp.production.infrastructure.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionProducerRepositoryException;
import de.uol.vpp.production.domain.repositories.IProductionProducerRepository;
import de.uol.vpp.production.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.production.infrastructure.entities.Production;
import de.uol.vpp.production.infrastructure.entities.ProductionProducer;
import de.uol.vpp.production.infrastructure.jpaRepositories.ProductionJpaRepository;
import de.uol.vpp.production.infrastructure.jpaRepositories.ProductionProducerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IProductionProducerRepository}
 */
@Service
@RequiredArgsConstructor
public class ProductionProducerRepositoryImpl implements IProductionProducerRepository {

    private final ProductionProducerJpaRepository productionProducerJpaRepository;
    private final ProductionJpaRepository productionJpaRepository;
    private final InfrastructureDomainConverter converter;

    @Override
    public void assignToInternal(Long producerInternalId, ProductionAggregate production) throws ProductionProducerRepositoryException {
        Optional<Production> productionJpa = productionJpaRepository.findById(new Production.ActionRequestTimestamp(production.getProductionActionRequestId().getValue(),
                production.getProductionStartTimestamp().getTimestamp()));
        if (productionJpa.isPresent()) {
            Optional<ProductionProducer> productionProducerJpa = productionProducerJpaRepository.findById(producerInternalId);
            if (productionProducerJpa.isPresent()) {
                if (productionProducerJpa.get().getProduction() == null) {
                    productionProducerJpa.get().setProduction(productionJpa.get());
                    productionProducerJpaRepository.save(productionProducerJpa.get());
                    productionJpa.get().getProducers().add(productionProducerJpa.get());
                    productionJpaRepository.save(productionJpa.get());
                } else {
                    throw new ProductionProducerRepositoryException(
                            String.format("Der Erzeugungswert konnte dem Erzeugungsaggregat %s nicht zugewiesen werden, da der Erzeugungswert bereits zugewiesen ist.",
                                    production.getProductionActionRequestId().getValue())
                    );
                }
            } else {
                throw new ProductionProducerRepositoryException("Der Erzeugungswert konnte nicht gefunden werden.");
            }
        } else {
            throw new ProductionProducerRepositoryException(
                    String.format("Das Erzeugungsaggregat %s (Zeitstempel %s) konnte für die Zuweisung des Erzeugungswert nicht gefunden werden.", production.getProductionActionRequestId().getValue(),
                            production.getProductionStartTimestamp().getTimestamp().toEpochSecond())
            );
        }
    }

    @Override
    public Long saveProductionProducerInternal(ProductionProducerEntity productionProducer) throws ProductionProducerRepositoryException {
        ProductionProducer jpaEntity = converter.toInfrastructure(productionProducer);
        ProductionProducer saved = productionProducerJpaRepository.save(jpaEntity);
        return saved.getInternalId();
    }

}
