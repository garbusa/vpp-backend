package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.exceptions.StorageServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für den Service von Speicheranlagen in der Serviceschicht
 */
public interface IStorageService {
    /**
     * Holt alle Speicheranlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste von Speicheranlagen
     * @throws StorageServiceException e
     */
    List<StorageEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws StorageServiceException;

    /**
     * Holt alle Speicheranlagen eines DK
     *
     * @param householdId Id des Haushalts
     * @return Liste von Speicheranlagen
     * @throws StorageServiceException e
     */
    List<StorageEntity> getAllByHouseholdId(String householdId) throws StorageServiceException;

    /**
     * Holt eine spezifische Speicheranlage
     *
     * @param storageId Id der Speicheranlage
     * @return Speicheranlage
     * @throws StorageServiceException e
     */
    StorageEntity get(String storageId) throws StorageServiceException;

    /**
     * Persistiert Speicheranlage und weist es einem DK zu
     *
     * @param domainEntity              Speicheranlage
     * @param decentralizedPowerPlantId Id des DK
     * @throws StorageServiceException e
     */
    void saveWithDecentralizedPowerPlant(StorageEntity domainEntity, String decentralizedPowerPlantId) throws StorageServiceException;

    /**
     * Persistiert Speicheranlage und weist es einem Haushalt zu
     *
     * @param domainEntity Speicheranlage
     * @param householdId  Id des Haushalt
     * @throws StorageServiceException e
     */
    void saveWithHousehold(StorageEntity domainEntity, String householdId) throws StorageServiceException;

    /**
     * Löscht eine Speicheranlage
     *
     * @param storageId           Id der Speicheranlage
     * @param virtualPowerPlantId Id des VK
     * @throws StorageServiceException e
     */
    void delete(String storageId, String virtualPowerPlantId) throws StorageServiceException;

    /**
     * Aktualisiert eine Speicheranlage
     *
     * @param storageId           Id der Speicheranlage
     * @param domainEntity        aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @throws StorageServiceException e
     */
    void update(String storageId, StorageEntity domainEntity, String virtualPowerPlantId) throws StorageServiceException;
}
