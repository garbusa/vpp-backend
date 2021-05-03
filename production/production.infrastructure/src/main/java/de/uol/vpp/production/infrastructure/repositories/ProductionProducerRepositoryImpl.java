package de.uol.vpp.production.infrastructure.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.exceptions.ProductionProducerRepositoryException;
import de.uol.vpp.production.domain.repositories.IProductionProducerRepository;
import de.uol.vpp.production.domain.valueobjects.ProductionStartTimestampVO;
import de.uol.vpp.production.domain.valueobjects.ProductionVirtualPowerPlantIdVO;
import de.uol.vpp.production.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.production.infrastructure.entities.Production;
import de.uol.vpp.production.infrastructure.entities.ProductionProducer;
import de.uol.vpp.production.infrastructure.jpaRepositories.ProductionJpaRepository;
import de.uol.vpp.production.infrastructure.jpaRepositories.ProductionProducerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductionProducerRepositoryImpl implements IProductionProducerRepository {

    private final ProductionProducerJpaRepository productionProducerJpaRepository;
    private final ProductionJpaRepository productionJpaRepository;
    private final InfrastructureDomainConverter converter;

    @Override
    public List<ProductionProducerEntity> getProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO virtualPowerPlantId, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException {
        try {
            List<ProductionProducerEntity> result = new ArrayList<>();
            Production production = productionJpaRepository.findById(new Production.ActionRequestTimestamp(virtualPowerPlantId.getValue(),
                    timestamp.getTimestamp())).orElse(null);
            if (production != null) {
                for (ProductionProducer productionProducer : productionProducerJpaRepository
                        .findAllByProduction(production)) {
                    result.add(converter.toDomain(productionProducer));
                }
            }
            return result;

        } catch (ProductionException e) {
            throw new ProductionProducerRepositoryException(e.getMessage(), e);
        }
    }

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
                            String.format("Erzeugungsanlage konnte Erzeugungsaggregat %s nicht zugewiesen werden, da Erzeugungsanlage bereits zugewiesen ist",
                                    production.getProductionActionRequestId().getValue())
                    );
                }
            } else {
                throw new ProductionProducerRepositoryException("Erzeugungsanlage konnte nicht gefunden werden");
            }
        } else {
            throw new ProductionProducerRepositoryException(
                    String.format("Erzeugungsaggregat %s (Zeitstempel %s) konnte nicht gefunden werden", production.getProductionActionRequestId().getValue(),
                            production.getProductionStartTimestamp().getTimestamp().toEpochSecond())
            );
        }
    }

    @Override
    public Long saveProductionProducerInternal(ProductionProducerEntity producer) throws ProductionProducerRepositoryException {
        ProductionProducer jpaEntity = converter.toInfrastructure(producer);
        ProductionProducer saved = productionProducerJpaRepository.save(jpaEntity);
        return saved.getInternalId();
    }

    @Override
    public void deleteProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO virtualPowerPlantId, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException {
        Optional<Production> jpaEntity = productionJpaRepository.findById(new Production.ActionRequestTimestamp(virtualPowerPlantId.getValue(),
                timestamp.getTimestamp()));
        if (jpaEntity.isPresent()) {
            productionProducerJpaRepository.deleteAllByProduction(jpaEntity.get());
        } else {
            throw new ProductionProducerRepositoryException(
                    String.format("Erzeugung der Erzeugungsanlagen konnte nicht gelöscht werden, da Erzeugungsaggregat %s (Zeitstempel %s) nicht gefunden werden konnte",
                            virtualPowerPlantId.getValue(), timestamp.getTimestamp().toEpochSecond())
            );
        }
    }
}
