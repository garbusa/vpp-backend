package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import de.uol.vpp.masterdata.domain.repositories.IStorageRepository;
import de.uol.vpp.masterdata.domain.repositories.StorageRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageStatusVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.Storage;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.StorageStatus;
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
                    .findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId());

            if (dpp.isPresent()) {
                List<StorageEntity> result = new ArrayList<>();
                for (Storage storage : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(storage));
                }
                return result;
            } else {
                throw new StorageRepositoryException(String.format("Can not find dpp %s to get all storages", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId()));
            }
        } catch (StorageException e) {
            throw new StorageRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<StorageEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws StorageRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneByBusinessKey(householdAggregate.getHouseholdId().getId());

            if (household.isPresent()) {
                List<StorageEntity> result = new ArrayList<>();
                for (Storage storage : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(storage));
                }
                return result;
            } else {
                throw new StorageRepositoryException(String.format("Can not find household %s to get all storages", householdAggregate.getHouseholdId().getId()));
            }
        } catch (StorageException e) {
            throw new StorageRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<StorageEntity> getById(StorageIdVO id) throws StorageRepositoryException {
        try {
            Optional<Storage> result = jpaRepository.findOneByBusinessKey(id.getId());
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
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId());
            if (dpp.isPresent()) {
                Optional<Storage> storage = jpaRepository.findOneByBusinessKey(storageEntity.getStorageId().getId());
                if (storage.isPresent()) {
                    if (storage.get().getDecentralizedPowerPlant() == null &&
                            storage.get().getHousehold() == null) {
                        storage.get().setDecentralizedPowerPlant(dpp.get());
                        jpaRepository.save(storage.get());
                        dpp.get().getStorages().add(storage.get());
                        decentralizedPowerPlantJpaRepository.save(dpp.get());
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
                        jpaRepository.save(storage.get());
                        household.get().getStorages().add(storage.get());
                        householdJpaRepository.save(household.get());
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
                    jpaRepository.delete(jpaEntity.get());
            } else {
                throw new StorageRepositoryException(
                        String.format("storage %s can not be found and can not be deleted", id.getId())
                );
            }
    }

    @Override
    public void updateStatus(StorageIdVO id, StorageStatusVO status) throws StorageRepositoryException {
            Optional<Storage> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntityOptional.isPresent()) {
                    Storage jpaEntity = jpaEntityOptional.get();
                    StorageStatus newStatus = new StorageStatus();
                    newStatus.setCapacity(status.getCapacity());
                    jpaEntity.setStorageStatus(newStatus);
                    jpaRepository.save(jpaEntity);
            } else {
                throw new StorageRepositoryException(
                        String.format("storage %s can not be found and can not be deleted", id.getId())
                );
            }
    }

    @Override
    public void update(StorageIdVO id, StorageEntity domainEntity) throws StorageRepositoryException {
            Optional<Storage> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntityOptional.isPresent()) {
                Storage jpaEntity = jpaEntityOptional.get();
                Storage updated = converter.toInfrastructure(domainEntity);
                jpaEntity.setBusinessKey(updated.getBusinessKey());
                jpaEntity.setStoragePower(updated.getStoragePower());
                jpaEntity.setStorageStatus(updated.getStorageStatus());
                jpaEntity.setStorageType(updated.getStorageType());
                jpaRepository.save(jpaEntity);
            } else {
                throw new StorageRepositoryException("failed to update storage. can not find storage entity");
            }
    }
}
