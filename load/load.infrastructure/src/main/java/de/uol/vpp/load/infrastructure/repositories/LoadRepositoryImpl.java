package de.uol.vpp.load.infrastructure.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.repositories.ILoadRepository;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;
import de.uol.vpp.load.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.jpaRepositories.LoadJpaRepository;
import de.uol.vpp.load.infrastructure.rest.MasterdataRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadRepositoryImpl implements ILoadRepository {

    private final LoadJpaRepository loadJpaRepository;
    private final InfrastructureDomainConverter converter;

    private static final int FORECAST_PERIODS = 24 * 4; //24h in 15minutes;
    private final MasterdataRestClient masterdataRestClient;

    @Override
    public List<LoadAggregate> getLoadsByActionRequestId(LoadActionRequestIdVO actionRequestBusinessKey) throws LoadRepositoryException {
        try {
            List<LoadAggregate> result = new ArrayList<>();
            for (ELoad load : loadJpaRepository
                    .findAllByActionRequestTimestamp_ActionRequestId(actionRequestBusinessKey.getId())) {
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


    @Override
    public void deleteLoadsByActionRequestId(LoadActionRequestIdVO actionRequestBusinessKey) throws LoadRepositoryException {
        List<ELoad> loads = loadJpaRepository.findAllByActionRequestTimestamp_ActionRequestId(
                actionRequestBusinessKey.getId());
        for (ELoad load : loads) {
            loadJpaRepository.delete(load);
        }
    }

    @Override
    public void updateLoad(LoadAggregate load) throws LoadRepositoryException {
        if (loadJpaRepository.findById(new ELoad.ActionRequestTimestamp(
                load.getLoadActionRequestId().getId(),
                load.getLoadStartTimestamp().getTimestamp()
        )).isPresent()) {
            loadJpaRepository.save(converter.toInfrastructure(load));
        } else {
            throw new LoadRepositoryException("failed to update load. not able to find.");
        }
    }


}
