package de.uol.vpp.load.infrastructure.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadHouseholdRepositoryException;
import de.uol.vpp.load.domain.repositories.ILoadHouseholdRepository;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;
import de.uol.vpp.load.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.entities.ELoadHousehold;
import de.uol.vpp.load.infrastructure.jpaRepositories.LoadHouseholdJpaRepository;
import de.uol.vpp.load.infrastructure.jpaRepositories.LoadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoadHouseholdRepositoryImpl implements ILoadHouseholdRepository {

    private final LoadHouseholdJpaRepository jpaRepository;
    private final LoadJpaRepository loadJpaRepository;
    private final InfrastructureDomainConverter converter;

    @Override
    public List<LoadHouseholdEntity> getLoadHouseholdsByVppTimestamp(LoadVirtualPowerPlantIdVO vppBusinessKey, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException {
        try {
            List<LoadHouseholdEntity> result = new ArrayList<>();
            ELoad load = loadJpaRepository.findById(new ELoad.VppTimestamp(vppBusinessKey.getId(),
                    timestamp.getTimestamp())).orElse(null);
            if (load != null) {
                for (ELoadHousehold loadHousehold : jpaRepository
                        .findAllByLoad(load)) {
                    result.add(converter.toDomain(loadHousehold));
                }
            }
            return result;

        } catch (LoadException e) {
            throw new LoadHouseholdRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void assign(LoadHouseholdEntity loadHousehold, LoadAggregate load) throws LoadHouseholdRepositoryException {
        Optional<ELoad> loadJpa = loadJpaRepository.findById(new ELoad.VppTimestamp(load.getLoadVirtualPowerPlantId().getId(),
                load.getLoadStartTimestamp().getTimestamp()));
        if (loadJpa.isPresent()) {
            Optional<ELoadHousehold> loadHouseholdJpa = jpaRepository.findById(
                    new ELoadHousehold.HouseholdTimestamp(loadHousehold.getLoadHouseholdId().getId(),
                            loadHousehold.getLoadHouseholdStartTimestamp().getTimestamp())
            );
            if (loadHouseholdJpa.isPresent()) {
                if (loadHouseholdJpa.get().getLoad() == null) {
                    loadHouseholdJpa.get().setLoad(loadJpa.get());
                    jpaRepository.save(loadHouseholdJpa.get());
                    loadJpa.get().getHouseholds().add(loadHouseholdJpa.get());
                    loadJpaRepository.save(loadJpa.get());
                } else {
                    throw new LoadHouseholdRepositoryException(
                            String.format("Failed to assign an entity for household load %s, the assigments have to be empty", loadHousehold.getLoadHouseholdId().getId())
                    );
                }
            } else {
                throw new LoadHouseholdRepositoryException(
                        String.format("Failed to fetch load household %s", loadHousehold.getLoadHouseholdId().getId())
                );
            }
        } else {
            throw new LoadHouseholdRepositoryException(
                    String.format("Load %s does not exist. Failed to fetch all household loads", load.getLoadVirtualPowerPlantId().getId())
            );
        }
    }

    @Override
    public void saveLoadHousehold(LoadHouseholdEntity load) throws LoadHouseholdRepositoryException {
        ELoadHousehold jpaEntity = converter.toInfrastructure(load);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void deleteHouseholdsByVppTimestamp(LoadVirtualPowerPlantIdVO vppBusinessKey, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException {
        Optional<ELoad> jpaEntity = loadJpaRepository.findById(new ELoad.VppTimestamp(vppBusinessKey.getId(),
                timestamp.getTimestamp()));
        if (jpaEntity.isPresent()) {
            jpaRepository.deleteAllByLoad(jpaEntity.get());
        } else {
            throw new LoadHouseholdRepositoryException(
                    String.format("failed to delete households by vpp id %s", vppBusinessKey.getId())
            );
        }
    }
}
