package de.uol.vpp.load.infrastructure.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadHouseholdRepositoryException;
import de.uol.vpp.load.domain.repositories.ILoadHouseholdRepository;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
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
    public List<LoadHouseholdEntity> getLoadHouseholdByActionRequestId(LoadActionRequestIdVO actionRequestId, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException {
        try {
            List<LoadHouseholdEntity> result = new ArrayList<>();
            ELoad load = loadJpaRepository.findById(new ELoad.ActionRequestTimestamp(actionRequestId.getId(),
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
    public void assignToInternal(Long loadHouseholdInternalId, LoadAggregate load) throws LoadHouseholdRepositoryException {
        Optional<ELoad> loadJpa = loadJpaRepository.findById(new ELoad.ActionRequestTimestamp(load.getLoadActionRequestId().getId(),
                load.getLoadStartTimestamp().getTimestamp()));
        if (loadJpa.isPresent()) {
            Optional<ELoadHousehold> loadHouseholdJpa = jpaRepository.findById(loadHouseholdInternalId);
            if (loadHouseholdJpa.isPresent()) {
                if (loadHouseholdJpa.get().getLoad() == null) {
                    loadHouseholdJpa.get().setLoad(loadJpa.get());
                    jpaRepository.save(loadHouseholdJpa.get());
                    loadJpa.get().getHouseholds().add(loadHouseholdJpa.get());
                    loadJpaRepository.save(loadJpa.get());
                } else {
                    throw new LoadHouseholdRepositoryException(
                            String.format("Haushaltslast konnte dem Lastaggregat %s nicht zugewiesen werden, da die Haushaltslast bereits zugewiesen wurde", load.getLoadActionRequestId().getId())
                    );
                }
            } else {
                throw new LoadHouseholdRepositoryException(
                        String.format("Haushaltslast %s (Zeistempel: %s) konnte nicht gefunden werden", load.getLoadActionRequestId(), load.getLoadStartTimestamp().getTimestamp().toEpochSecond())
                );
            }
        } else {
            throw new LoadHouseholdRepositoryException(
                    String.format("Haushaltslast konnte dem Lastaggregat %s nicht zugeiwesen werden, da Last nicht gefunden wurde", load.getLoadActionRequestId().getId())
            );
        }
    }

    @Override
    public Long saveLoadHouseholdInternal(LoadHouseholdEntity load) throws LoadHouseholdRepositoryException {
        ELoadHousehold jpaEntity = converter.toInfrastructure(load);
        ELoadHousehold saved = jpaRepository.save(jpaEntity);
        return saved.getInternalId();
    }

    @Override
    public void deleteHouseholdsByActionRequestId(LoadActionRequestIdVO actionRequestId, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException {
        Optional<ELoad> jpaEntity = loadJpaRepository.findById(new ELoad.ActionRequestTimestamp(actionRequestId.getId(),
                timestamp.getTimestamp()));
        if (jpaEntity.isPresent()) {
            jpaRepository.deleteAllByLoad(jpaEntity.get());
        } else {
            throw new LoadHouseholdRepositoryException(
                    String.format("Haushaltslaste konnten nicht gelöscht werden, da ActionRequest-Lastaggregat %s nicht gefunden wurde", actionRequestId.getId())
            );
        }
    }
}
