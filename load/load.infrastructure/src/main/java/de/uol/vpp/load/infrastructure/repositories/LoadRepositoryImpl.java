package de.uol.vpp.load.infrastructure.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.repositories.ILoadRepository;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;
import de.uol.vpp.load.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.jpaRepositories.LoadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementierung der Haushaltslast-Repository Schnittstellendefinition
 * {@link ILoadRepository}
 */
@Service
@RequiredArgsConstructor
public class LoadRepositoryImpl implements ILoadRepository {

    private final LoadJpaRepository loadJpaRepository;
    private final InfrastructureDomainConverter converter;

    @Override
    public List<LoadAggregate> getLoadsByActionRequestId(LoadActionRequestIdVO actionRequestId) throws LoadRepositoryException {
        try {
            List<LoadAggregate> result = new ArrayList<>();
            for (ELoad load : loadJpaRepository
                    .findAllByActionRequestTimestamp_ActionRequestId(actionRequestId.getId())) {
                result.add(converter.toDomain(load));
            }
            return result;
        } catch (LoadException e) {
            throw new LoadRepositoryException(e.getMessage(), e);
        }
    }


    @Override
    public void saveLoad(LoadAggregate load) throws LoadRepositoryException {
        ELoad jpaEntity = converter.toInfrastructure(load);
        loadJpaRepository.save(jpaEntity);
    }


}
