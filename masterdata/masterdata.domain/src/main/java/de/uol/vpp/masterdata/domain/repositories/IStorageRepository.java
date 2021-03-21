package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageStatusVO;

import java.util.List;
import java.util.Optional;

public interface IStorageRepository {
    List<StorageEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException;

    List<StorageEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws StorageRepositoryException;

    Optional<StorageEntity> getById(StorageIdVO id) throws StorageRepositoryException;

    void save(StorageEntity storageEntity) throws StorageRepositoryException;

    void assignToDecentralizedPowerPlant(StorageEntity producerEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException;

    void assignToHousehold(StorageEntity entity, HouseholdAggregate householdAggregate) throws StorageRepositoryException;

    void deleteById(StorageIdVO id) throws StorageRepositoryException;

    void updateStatus(StorageIdVO storageIdVO, StorageStatusVO storageStatusVO) throws StorageRepositoryException;

    void update(StorageIdVO id, StorageEntity domainEntity) throws StorageRepositoryException;
}
