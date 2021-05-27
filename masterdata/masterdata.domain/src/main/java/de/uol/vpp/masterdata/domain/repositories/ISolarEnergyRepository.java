package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.SolarEnergyIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das Repository von Solaranlagen
 */
public interface ISolarEnergyRepository {
    /**
     * Holt alle Solaranlagen eines DK
     *
     * @param decentralizedPowerPlantAggregate DK
     * @return Liste von Solaranlagen
     * @throws ProducerRepositoryException e
     */
    List<SolarEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Holt alle Solaranlagen eines Haushalts
     *
     * @param householdAggregate Haushalt
     * @return Liste von Solaranlagen
     * @throws ProducerRepositoryException e
     */
    List<SolarEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Holt eine Solaranlage
     *
     * @param id Id der Solaranlage
     * @return Solaranlage
     * @throws ProducerRepositoryException e
     */
    Optional<SolarEnergyEntity> getById(SolarEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Persistiert eine Solaranlage
     *
     * @param domainEntity Solaranlage
     * @throws ProducerRepositoryException e
     */
    void save(SolarEnergyEntity domainEntity) throws ProducerRepositoryException;

    /**
     * Weist Solaranlage einem DK zu
     *
     * @param domainEntity                     Solaranlage
     * @param decentralizedPowerPlantAggregate DK
     * @throws ProducerRepositoryException e
     */
    void assignToDecentralizedPowerPlant(SolarEnergyEntity domainEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Weist Solaranlage einem Haushalt zu
     *
     * @param domainEntity       Solaranlage
     * @param householdAggregate Haushalt
     * @throws ProducerRepositoryException e
     */
    void assignToHousehold(SolarEnergyEntity domainEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Löscht eine alternative Erzeugunsanlage
     *
     * @param id Id der Solaranlage
     * @throws ProducerRepositoryException e
     */
    void deleteById(SolarEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Aktualisiert eine Solaranlage
     *
     * @param id           Id der Solaranlage
     * @param domainEntity Solaranlage
     * @throws ProducerRepositoryException e
     */
    void update(SolarEnergyIdVO id, SolarEnergyEntity domainEntity) throws ProducerRepositoryException;
}
