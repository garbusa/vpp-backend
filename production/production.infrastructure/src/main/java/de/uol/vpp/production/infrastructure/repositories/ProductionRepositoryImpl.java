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

    @Override
    public void deleteProductionsByActionRequestId(ProductionActionRequestIdVO actionRequestId) throws ProductionRepositoryException {
        List<Production> productions = productionJpaRepository.findAllByActionRequestTimestamp_ActionRequestId(
                actionRequestId.getValue());
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
            throw new ProductionRepositoryException(
                    String.format("Erzeugungsaggregat %s (Zeitstempel %s) konnte nicht aktualisiert werden, da Erzeugungsaggregat nicht gefunden werden konnte",
                            production.getProductionActionRequestId().getValue(), production.getProductionStartTimestamp().getTimestamp().toEpochSecond())
            );
        }
    }
}
