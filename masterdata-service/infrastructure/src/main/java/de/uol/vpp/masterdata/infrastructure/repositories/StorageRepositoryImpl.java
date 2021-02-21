package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StorageRepositoryImpl implements IStorageRepository {

    private final StorageJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<StorageEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                .findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId());

        if (dpp.isPresent()) {
            return jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())
                    .stream().map(converter::toDomain).collect(Collectors.toList());
        } else {
            throw new StorageRepositoryException(String.format("Can not find dpp %s to get all storages", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId()));
        }

    }

    @Override
    public List<StorageEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws StorageRepositoryException {
        Optional<Household> household = householdJpaRepository
                .findOneByBusinessKey(householdAggregate.getHouseholdId().getId());

        if (household.isPresent()) {
            return jpaRepository.findAllByHousehold(household.get())
                    .stream().map(converter::toDomain).collect(Collectors.toList());
        } else {
            throw new StorageRepositoryException(String.format("Can not find household %s to get all storages", householdAggregate.getHouseholdId().getId()));
        }
    }

    @Override
    public Optional<StorageEntity> getById(StorageIdVO id) throws StorageRepositoryException {
        Optional<Storage> result = jpaRepository.findOneByBusinessKey(id.getId());
        return result.map(converter::toDomain);
    }

    @Override
    public void save(StorageEntity storageEntity) throws StorageRepositoryException {
        try {
            Storage jpaEntity = converter.toInfrastructure(storageEntity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (Exception e) {
            throw new StorageRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void assignToDecentralizedPowerPlant(StorageEntity storageEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId());
        if (dpp.isPresent()) {
            Optional<Storage> storage = jpaRepository.findOneByBusinessKey(storageEntity.getStorageId().getId());
            if (storage.isPresent()) {
                if (storage.get().getDecentralizedPowerPlant() == null &&
                        storage.get().getHousehold() == null) {
                    storage.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.saveAndFlush(storage.get());
                    dpp.get().getStorages().add(storage.get());
                    decentralizedPowerPlantJpaRepository.saveAndFlush(dpp.get());
                } else {
                    throw new StorageRepositoryException(
                            String.format("To assign an entity for storage %s, the assigments have to be empty", storageEntity.getStorageId().getId())
                    );
                }
            } else {
                throw new StorageRepositoryException(
                        String.format("Failed to fetch storage %s", storageEntity.getStorageId().getId())
                );
            }
        } else {
            throw new StorageRepositoryException(
                    String.format("Dpp %s does not exist. Failed to fetch all storage", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId())
            );
        }
    }

    @Override
    public void assignToHousehold(StorageEntity storageEntity, HouseholdAggregate householdAggregate) throws StorageRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneByBusinessKey(householdAggregate.getHouseholdId().getId());
        if (household.isPresent()) {
            Optional<Storage> storage = jpaRepository.findOneByBusinessKey(storageEntity.getStorageId().getId());
            if (storage.isPresent()) {
                if (storage.get().getDecentralizedPowerPlant() == null &&
                        storage.get().getHousehold() == null) {
                    storage.get().setHousehold(household.get());
                    jpaRepository.saveAndFlush(storage.get());
                    household.get().getStorages().add(storage.get());
                    householdJpaRepository.saveAndFlush(household.get());
                } else {
                    throw new StorageRepositoryException(
                            String.format("To assign an entity for storage %s, the assigments have to be empty", storageEntity.getStorageId().getId())
                    );
                }
            } else {
                throw new StorageRepositoryException(
                        String.format("Failed to fetch storage %s", storageEntity.getStorageId().getId())
                );
            }
        } else {
            throw new StorageRepositoryException(
                    String.format("Household %s does not exist. Failed to fetch all storage", householdAggregate.getHouseholdId().getId())
            );
        }
    }

    @Override
    public void deleteById(StorageIdVO id) throws StorageRepositoryException {
        Optional<Storage> jpaEntity = jpaRepository.findOneByBusinessKey(id.getId());
        if (jpaEntity.isPresent()) {
            try {
                jpaRepository.delete(jpaEntity.get());
            } catch (Exception e) {
                throw new StorageRepositoryException(e.getMessage(), e);
            }
        } else {
            throw new StorageRepositoryException(
                    String.format("storage %s can not be found and can not be deleted", id.getId())
            );
        }
    }
}
