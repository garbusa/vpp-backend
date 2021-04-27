package de.uol.vpp.production.service.services;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.exceptions.ProductionRepositoryException;
import de.uol.vpp.production.domain.exceptions.ProductionServiceException;
import de.uol.vpp.production.domain.repositories.IProductionRepository;
import de.uol.vpp.production.domain.services.IProductionService;
import de.uol.vpp.production.domain.valueobjects.ProductionActionRequestIdVO;
import de.uol.vpp.production.infrastructure.scheduler.ProductionScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements IProductionService {

    private final IProductionRepository repository;
    private final ProductionScheduler scheduler;

    @Override
    public List<ProductionAggregate> getProductionsByActionRequestId(String actionRequestBusinessKey) throws ProductionServiceException {
        try {
            return repository.getProductions(new ProductionActionRequestIdVO(actionRequestBusinessKey));
        } catch (ProductionRepositoryException | ProductionException e) {
            throw new ProductionServiceException(e.getMessage(), e);
        }
    }
}
