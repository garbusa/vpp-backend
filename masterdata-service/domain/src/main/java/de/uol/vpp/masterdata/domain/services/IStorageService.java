package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.StorageEntity;

import java.util.List;

public interface IStorageService {
    List<StorageEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws StorageServiceException;

    List<StorageEntity> getAllByHouseholdId(String householdBusinessKey) throws StorageServiceException;

    StorageEntity get(String businessKey) throws StorageServiceException;

    void saveWithDecentralizedPowerPlant(StorageEntity domainEntity, String dppBusinessKey) throws StorageServiceException;

    void saveWithHousehold(StorageEntity domainEntity, String householdBusinessKey) throws StorageServiceException;

    void delete(String businessKey) throws StorageServiceException;
}
