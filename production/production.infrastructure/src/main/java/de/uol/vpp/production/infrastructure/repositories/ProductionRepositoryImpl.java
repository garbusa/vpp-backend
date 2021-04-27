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

@Service
@RequiredArgsConstructor
public class ProductionRepositoryImpl implements IProductionRepository {

    private final ProductionJpaRepository productionJpaRepository;
    private final InfrastructureDomainConverter converter;

    @Override
    public List<ProductionAggregate> getProductions(ProductionActionRequestIdVO actionRequestBusinessKey) throws ProductionRepositoryException {
        try {
            List<ProductionAggregate> result = new ArrayList<>();
            for (Production production : productionJpaRepository
                    .findAllByActionRequestTimestamp_ActionRequestId(actionRequestBusinessKey.getValue())) {
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

    @Override
    public void deleteProductionsByActionRequestId(ProductionActionRequestIdVO actionRequestBusinessKey) throws ProductionRepositoryException {
        List<Production> productions = productionJpaRepository.findAllByActionRequestTimestamp_ActionRequestId(
                actionRequestBusinessKey.getValue());
        for (Production production : productions) {
            productionJpaRepository.delete(production);
        }
    }

    @Override
    public void updateProduction(ProductionAggregate production) throws ProductionRepositoryException {
        if (productionJpaRepository.findById(new Production.ActionRequestTimestamp(
                production.getProductionActionRequestId().getValue(),
                production.getProductionStartTimestamp().getTimestamp()
        )).isPresent()) {
            productionJpaRepository.save(converter.toInfrastructure(production));
        } else {
            throw new ProductionRepositoryException("failed to update production. not able to find.");
        }
    }
}
