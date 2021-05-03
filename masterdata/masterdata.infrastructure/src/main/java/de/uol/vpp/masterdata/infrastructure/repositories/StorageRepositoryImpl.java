package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import de.uol.vpp.masterdata.domain.repositories.IStorageRepository;
import de.uol.vpp.masterdata.domain.repositories.StorageRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.Storage;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.StorageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StorageRepositoryImpl implements IStorageRepository {

    private final StorageJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<StorageEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                    .findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());

            if (dpp.isPresent()) {
                List<StorageEntity> result = new ArrayList<>();
                for (Storage storage : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(storage));
                }
                return result;
            } else {
                throw new StorageRepositoryException(String.format("DK mit der ID %s konnte nicht gefunden werden um alle Speicher zu laden", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue()));
            }
        } catch (StorageException e) {
            throw new StorageRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<StorageEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws StorageRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneById(householdAggregate.getHouseholdId().getValue());

            if (household.isPresent()) {
                List<StorageEntity> result = new ArrayList<>();
                for (Storage storage : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(storage));
                }
                return result;
            } else {
                throw new StorageRepositoryException(String.format("Haushalt mit der ID %s konnte nicht gefunden werden um alle Speicher zu laden", householdAggregate.getHouseholdId().getValue()));
            }
        } catch (StorageException e) {
            throw new StorageRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<StorageEntity> getById(StorageIdVO id) throws StorageRepositoryException {
        try {
            Optional<Storage> result = jpaRepository.findOneById(id.getValue());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (StorageException e) {
            throw new StorageRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(StorageEntity storageEntity) throws StorageRepositoryException {
        Storage jpaEntity = converter.toInfrastructure(storageEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assignToDecentralizedPowerPlant(StorageEntity storageEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
        if (dpp.isPresent()) {
            Optional<Storage> storage = jpaRepository.findOneById(storageEntity.getStorageId().getValue());
            if (storage.isPresent()) {
                if (storage.get().getDecentralizedPowerPlant() == null &&
                        storage.get().getHousehold() == null) {
                    storage.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(storage.get());
                    dpp.get().getStorages().add(storage.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new StorageRepositoryException(
                            String.format("Zuweisung des Speichers %s an ein DK ist fehlgeschlagen, da dieser Speicher bereits zugewiesen wurde.", storageEntity.getStorageId().getValue())
                    );
                }
            } else {
                throw new StorageRepositoryException(
                        String.format("Abfrage eines Speichers %s ist fehlgeschlagen", storageEntity.getStorageId().getValue())
                );
            }
        } else {
            throw new StorageRepositoryException(
                    String.format("DK %s konnte nicht gefunden werden um Speicher zu laden", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void assignToHousehold(StorageEntity storageEntity, HouseholdAggregate householdAggregate) throws StorageRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneById(householdAggregate.getHouseholdId().getValue());
        if (household.isPresent()) {
            Optional<Storage> storage = jpaRepository.findOneById(storageEntity.getStorageId().getValue());
            if (storage.isPresent()) {
                if (storage.get().getDecentralizedPowerPlant() == null &&
                        storage.get().getHousehold() == null) {
                    storage.get().setHousehold(household.get());
                    jpaRepository.save(storage.get());
                    household.get().getStorages().add(storage.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new StorageRepositoryException(
                            String.format("Zuweisung des Speichers %s an ein Haushalt ist fehlgeschlagen, da dieser Speicher bereits zugewiesen wurde.", storageEntity.getStorageId().getValue())
                    );
                }
            } else {
                throw new StorageRepositoryException(
                        String.format("Abfrage des Speichers %s ist fehlgeschlagen", storageEntity.getStorageId().getValue())
                );
            }
        } else {
            throw new StorageRepositoryException(
                    String.format("Haushalt %s konnte nicht gefunden werden um alle Speicher abzufragen", householdAggregate.getHouseholdId().getValue())
            );
        }
    }

    @Override
    public void deleteById(StorageIdVO id) throws StorageRepositoryException {
        Optional<Storage> jpaEntity = jpaRepository.findOneById(id.getValue());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new StorageRepositoryException(
                    String.format("Speicher %s konnte nicht gefunden werden um den Speicher zu löschen", id.getValue())
            );
        }
    }


    @Override
    public void update(StorageIdVO id, StorageEntity domainEntity) throws StorageRepositoryException {
        Optional<Storage> jpaEntityOptional = jpaRepository.findOneById(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            Storage jpaEntity = jpaEntityOptional.get();
            Storage updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setId(updated.getId());
            jpaEntity.setRatedPower(updated.getRatedPower());
            jpaEntity.setCapacity(updated.getCapacity());
            jpaEntity.setLoadTimeHour(updated.getLoadTimeHour());
            jpaRepository.save(jpaEntity);
        } else {
            throw new StorageRepositoryException(String.format("Speicher %s konnte nicht gefunden werden um Speicher zu aktualisieren", id.getValue()));
        }
    }
}
