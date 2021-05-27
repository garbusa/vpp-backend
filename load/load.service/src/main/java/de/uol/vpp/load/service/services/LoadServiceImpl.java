package de.uol.vpp.load.service.services;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.exceptions.LoadServiceException;
import de.uol.vpp.load.domain.repositories.ILoadRepository;
import de.uol.vpp.load.domain.services.ILoadService;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementierung der Lastaggregat-Service Schnittstellendefinition
 * Siehe {@link ILoadService}
 */
@Service
@RequiredArgsConstructor
public class LoadServiceImpl implements ILoadService {

    private final ILoadRepository repository;

    @Override
    public List<LoadAggregate> getLoadsByActionRequestId(String actionRequestId) throws LoadServiceException {
        try {
            return repository.getLoadsByActionRequestId(new LoadActionRequestIdVO(actionRequestId));
        } catch (LoadRepositoryException | LoadException e) {
            throw new LoadServiceException(e.getMessage(), e);
        }
    }
}
