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
    public List<ProductionProducerEntity> getProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO vppBusinessKey, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException {
        try {
            List<ProductionProducerEntity> result = new ArrayList<>();
            Production production = productionJpaRepository.findById(new Production.ActionRequestTimestamp(vppBusinessKey.getValue(),
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
    public void assign(Long producerInternalId, ProductionAggregate production) throws ProductionProducerRepositoryException {
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
                            "Failed to assign an entity for producer %s, the assigments have to be empty"
                    );
                }
            } else {
                throw new ProductionProducerRepositoryException(
                        "Failed to fetch producer by actionRequestId %s"
                );
            }
        } else {
            throw new ProductionProducerRepositoryException(
                    String.format("Production for actionRequestId %s does not exist. Failed to fetch all producers", production.getProductionActionRequestId().getValue())
            );
        }
    }

    @Override
    public Long saveProductionProducer(ProductionProducerEntity producer) throws ProductionProducerRepositoryException {
        ProductionProducer jpaEntity = converter.toInfrastructure(producer);
        ProductionProducer saved = productionProducerJpaRepository.save(jpaEntity);
        return saved.getInternalId();
    }

    @Override
    public void deleteProductionProducersByVppTimestamp(ProductionVirtualPowerPlantIdVO vppBusinessKey, ProductionStartTimestampVO timestamp) throws ProductionProducerRepositoryException {
        Optional<Production> jpaEntity = productionJpaRepository.findById(new Production.ActionRequestTimestamp(vppBusinessKey.getValue(),
                timestamp.getTimestamp()));
        if (jpaEntity.isPresent()) {
            productionProducerJpaRepository.deleteAllByProduction(jpaEntity.get());
        } else {
            throw new ProductionProducerRepositoryException(
                    String.format("failed to delete producers by vpp actionRequestId %s", vppBusinessKey.getValue())
            );
        }
    }
}
