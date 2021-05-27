package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.exceptions.StorageRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das Repository von Speicheranlagen
 */
public interface IStorageRepository {
    /**
     * Holt alle Speicheranlagen eines DK
     *
     * @param decentralizedPowerPlantAggregate DK
     * @return Liste von Speicheranlagen
     * @throws StorageRepositoryException e
     */
    List<StorageEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException;

    /**
     * Holt alle Speicheranlagen eines Haushalts
     *
     * @param householdAggregate Haushalt
     * @return Liste von Speicheranlagen
     * @throws StorageRepositoryException e
     */
    List<StorageEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws StorageRepositoryException;

    /**
     * Holt eine Speicheranlage
     *
     * @param id Id der Speicheranlage
     * @return Speicheranlage
     * @throws StorageRepositoryException e
     */
    Optional<StorageEntity> getById(StorageIdVO id) throws StorageRepositoryException;

    /**
     * Persistiert eine Speicheranlage
     *
     * @param domainEntity Speicheranlage
     * @throws StorageRepositoryException e
     */
    void save(StorageEntity domainEntity) throws StorageRepositoryException;

    /**
     * Weist Speicheranlage einem DK zu
     *
     * @param domainEntity                     Speicheranlage
     * @param decentralizedPowerPlantAggregate DK
     * @throws StorageRepositoryException e
     */
    void assignToDecentralizedPowerPlant(StorageEntity domainEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws StorageRepositoryException;

    /**
     * Weist Speicheranlage einem Haushalt zu
     *
     * @param domainEntity       Speicheranlage
     * @param householdAggregate Haushalt
     * @throws StorageRepositoryException e
     */
    void assignToHousehold(StorageEntity domainEntity, HouseholdAggregate householdAggregate) throws StorageRepositoryException;

    /**
     * Löscht eine alternative Erzeugunsanlage
     *
     * @param id Id der Speicheranlage
     * @throws StorageRepositoryException e
     */
    void deleteById(StorageIdVO id) throws StorageRepositoryException;

    /**
     * Aktualisiert eine Speicheranlage
     *
     * @param id           Id der Speicheranlage
     * @param domainEntity Speicheranlage
     * @throws StorageRepositoryException e
     */
    void update(StorageIdVO id, StorageEntity domainEntity) throws StorageRepositoryException;
}
