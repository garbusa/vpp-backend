package de.uol.vpp.load.infrastructure.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadHouseholdRepositoryException;
import de.uol.vpp.load.domain.repositories.ILoadHouseholdRepository;
import de.uol.vpp.load.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.entities.ELoadHousehold;
import de.uol.vpp.load.infrastructure.jpaRepositories.LoadHouseholdJpaRepository;
import de.uol.vpp.load.infrastructure.jpaRepositories.LoadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementierung der Haushaltslast-Repository Schnittstellendefinition
 * {@link ILoadHouseholdRepository}
 */
@Service
@RequiredArgsConstructor
public class LoadHouseholdRepositoryImpl implements ILoadHouseholdRepository {

    private final LoadHouseholdJpaRepository jpaRepository;
    private final LoadJpaRepository loadJpaRepository;
    private final InfrastructureDomainConverter converter;

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
                            String.format("Die Haushaltslast konnte dem Lastaggregat %s nicht zugewiesen werden, da die Haushaltslast bereits zugewiesen wurde.", load.getLoadActionRequestId().getId())
                    );
                }
            } else {
                throw new LoadHouseholdRepositoryException(
                        String.format("Die Haushaltslast %s (Zeistempel: %s) konnte nicht gefunden werden.", load.getLoadActionRequestId(), load.getLoadStartTimestamp().getTimestamp().toEpochSecond())
                );
            }
        } else {
            throw new LoadHouseholdRepositoryException(
                    String.format("Die Haushaltslast konnte dem Lastaggregat %s nicht zugewiesen werden, da das Lastaggregat nicht gefunden wurde.", load.getLoadActionRequestId().getId())
            );
        }
    }

    @Override
    public Long saveLoadHouseholdInternal(LoadHouseholdEntity load) throws LoadHouseholdRepositoryException {
        ELoadHousehold jpaEntity = converter.toInfrastructure(load);
        ELoadHousehold saved = jpaRepository.save(jpaEntity);
        return saved.getInternalId();
    }
}
