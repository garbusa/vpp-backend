package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.StorageEntity;

import java.util.List;

public interface IStorageService {
    List<StorageEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws StorageServiceException;

    List<StorageEntity> getAllByHouseholdId(String householdId) throws StorageServiceException;

    StorageEntity get(String storageId) throws StorageServiceException;

    void saveWithDecentralizedPowerPlant(StorageEntity domainEntity, String decentralizedPowerPlantId) throws StorageServiceException;

    void saveWithHousehold(StorageEntity domainEntity, String householdId) throws StorageServiceException;

    void delete(String storageId, String virtualPowerPlantId) throws StorageServiceException;

    void update(String storageId, StorageEntity domainEntity, String virtualPowerPlantId) throws StorageServiceException;
}
