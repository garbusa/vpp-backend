package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity;
import de.uol.vpp.masterdata.domain.valueobjects.SolarEnergyIdVO;

import java.util.List;
import java.util.Optional;

public interface ISolarEnergyRepository {
    List<SolarEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    List<SolarEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    Optional<SolarEnergyEntity> getById(SolarEnergyIdVO id) throws ProducerRepositoryException;

    void save(SolarEnergyEntity SolarEnergyEntity) throws ProducerRepositoryException;

    void assignToDecentralizedPowerPlant(SolarEnergyEntity SolarEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    void assignToHousehold(SolarEnergyEntity entity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    void deleteById(SolarEnergyIdVO id) throws ProducerRepositoryException;

    void update(SolarEnergyIdVO id, SolarEnergyEntity domainEntity) throws ProducerRepositoryException;
}
