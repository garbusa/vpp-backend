package de.uol.vpp.production.infrastructure.repositories;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.exceptions.ProductionRepositoryException;
import de.uol.vpp.production.domain.repositories.IProductionRepository;
import de.uol.vpp.production.domain.valueobjects.ProductionActionRequestIdVO;
import de.uol.vpp.production.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.production.infrastructure.entities.Production;
import de.uol.vpp.production.infrastructure.jpaRepositories.ProductionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementierung der Schnittstellendefinition {@link IProductionRepository}
 */
@Service
@RequiredArgsConstructor
public class ProductionRepositoryImpl implements IProductionRepository {

    private final ProductionJpaRepository productionJpaRepository;
    private final InfrastructureDomainConverter converter;

    @Override
    public List<ProductionAggregate> getProductions(ProductionActionRequestIdVO actionRequestId) throws ProductionRepositoryException {
        try {
            List<ProductionAggregate> result = new ArrayList<>();
            for (Production production : productionJpaRepository
                    .findAllByActionRequestTimestamp_ActionRequestId(actionRequestId.getValue())) {
                result.add(converter.toDomain(production));
            }
            return result;
        } catch (ProductionException e) {
            throw new ProductionRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void saveProduction(ProductionAggregate production) throws ProductionRepositoryException {
        Production jpaEntity = converter.toInfrastructure(production);
        productionJpaRepository.save(jpaEntity);
    }
}
