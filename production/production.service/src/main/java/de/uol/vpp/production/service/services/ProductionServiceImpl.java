package de.uol.vpp.production.service.services;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.exceptions.ProductionRepositoryException;
import de.uol.vpp.production.domain.exceptions.ProductionServiceException;
import de.uol.vpp.production.domain.repositories.IProductionRepository;
import de.uol.vpp.production.domain.services.IProductionService;
import de.uol.vpp.production.domain.valueobjects.ProductionActionRequestIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementierung der Schnittstellendefinition {@link IProductionService}
 */
@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements IProductionService {

    private final IProductionRepository repository;

    @Override
    public List<ProductionAggregate> getProductionsByActionRequestId(String actionRequestId) throws ProductionServiceException {
        try {
            return repository.getProductions(new ProductionActionRequestIdVO(actionRequestId));
        } catch (ProductionRepositoryException | ProductionException e) {
            throw new ProductionServiceException(e.getMessage(), e);
        }
    }
}
