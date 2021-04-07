package de.uol.vpp.load.infrastructure.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.repositories.ILoadRepository;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;
import de.uol.vpp.load.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.jpaRepositories.LoadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadRepositoryImpl implements ILoadRepository {

    private final LoadJpaRepository loadJpaRepository;
    private final InfrastructureDomainConverter converter;


    @Override
    public List<LoadAggregate> getCurrentVppLoads(LoadVirtualPowerPlantIdVO vppBusinessKey) throws LoadRepositoryException {
        try {
            List<LoadAggregate> result = new ArrayList<>();
            for (ELoad load : loadJpaRepository
                    .findAllByVppTimestampVppBusinessKeyAndOutdated(vppBusinessKey.getId(), false)) {
                result.add(converter.toDomain(load));
            }
            return result;
        } catch (LoadException e) {
            throw new LoadRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<LoadAggregate> getForecastedVppLoads(LoadVirtualPowerPlantIdVO vppBusinessKey) throws LoadRepositoryException {
        try {
            List<LoadAggregate> result = new ArrayList<>();
            for (ELoad load : loadJpaRepository
                    .findAllByVppTimestampVppBusinessKeyAndOutdatedAndForecasted(vppBusinessKey.getId(), false, true)) {
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
    public void outdateLoad(LoadVirtualPowerPlantIdVO vppBusinessKey, ZonedDateTime compare) throws LoadRepositoryException {
        List<ELoad> eLoads = loadJpaRepository.findAllByVppTimestamp_VppBusinessKeyAndVppTimestamp_StartTimestampLessThanEqual(
                vppBusinessKey.getId(), compare
        );
        for (ELoad eLoad : eLoads) {
            if (eLoad.isForecasted()) {
                eLoad.setOutdated(true);
                loadJpaRepository.save(eLoad);
            }
        }
    }

    @Override
    public void deleteLoadsByVppId(LoadVirtualPowerPlantIdVO vppBusinessKey) throws LoadRepositoryException {
        List<ELoad> loads = loadJpaRepository.findAllByVppTimestampVppBusinessKeyAndOutdated(
                vppBusinessKey.getId(), true);
        for (ELoad load : loads) {
            loadJpaRepository.delete(load);
        }
    }

    @Override
    public void updateLoad(LoadAggregate load) throws LoadRepositoryException {
        if (loadJpaRepository.findById(new ELoad.VppTimestamp(
                load.getLoadVirtualPowerPlantId().getId(),
                load.getLoadStartTimestamp().getTimestamp()
        )).isPresent()) {
            loadJpaRepository.save(converter.toInfrastructure(load));
        } else {
            throw new LoadRepositoryException("failed to update load. not able to find.");
        }
    }
}
