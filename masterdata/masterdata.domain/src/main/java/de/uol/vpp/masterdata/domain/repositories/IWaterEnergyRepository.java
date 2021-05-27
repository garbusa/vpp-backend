package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.WaterEnergyIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das Repository von Wasserkraftanlagen
 */
public interface IWaterEnergyRepository {
    /**
     * Holt alle Wasserkraftanlagen eines DK
     *
     * @param decentralizedPowerPlantAggregate DK
     * @return Liste von Wasserkraftanlagen
     * @throws ProducerRepositoryException e
     */
    List<WaterEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Holt alle Wasserkraftanlagen eines Haushalts
     *
     * @param householdAggregate Haushalt
     * @return Liste von Wasserkraftanlagen
     * @throws ProducerRepositoryException e
     */
    List<WaterEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Holt eine Wasserkraftanlage
     *
     * @param id Id der Wasserkraftanlage
     * @return Wasserkraftanlage
     * @throws ProducerRepositoryException e
     */
    Optional<WaterEnergyEntity> getById(WaterEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Persistiert eine Wasserkraftanlage
     *
     * @param domainEntity Wasserkraftanlage
     * @throws ProducerRepositoryException e
     */
    void save(WaterEnergyEntity domainEntity) throws ProducerRepositoryException;

    /**
     * Weist Wasserkraftanlage einem DK zu
     *
     * @param domainEntity                     Wasserkraftanlage
     * @param decentralizedPowerPlantAggregate DK
     * @throws ProducerRepositoryException e
     */
    void assignToDecentralizedPowerPlant(WaterEnergyEntity domainEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Weist Wasserkraftanlage einem Haushalt zu
     *
     * @param domainEntity       Wasserkraftanlage
     * @param householdAggregate Haushalt
     * @throws ProducerRepositoryException e
     */
    void assignToHousehold(WaterEnergyEntity domainEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Löscht eine alternative Erzeugunsanlage
     *
     * @param id Id der Wasserkraftanlage
     * @throws ProducerRepositoryException e
     */
    void deleteById(WaterEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Aktualisiert eine Wasserkraftanlage
     *
     * @param id           Id der Wasserkraftanlage
     * @param domainEntity Wasserkraftanlage
     * @throws ProducerRepositoryException e
     */
    void update(WaterEnergyIdVO id, WaterEnergyEntity domainEntity) throws ProducerRepositoryException;
}
