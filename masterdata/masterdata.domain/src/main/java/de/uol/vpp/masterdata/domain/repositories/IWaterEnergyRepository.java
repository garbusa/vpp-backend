package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;
import de.uol.vpp.masterdata.domain.valueobjects.WaterEnergyIdVO;

import java.util.List;
import java.util.Optional;

public interface IWaterEnergyRepository {
    List<WaterEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    List<WaterEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    Optional<WaterEnergyEntity> getById(WaterEnergyIdVO id) throws ProducerRepositoryException;

    void save(WaterEnergyEntity WaterEnergyEntity) throws ProducerRepositoryException;

    void assignToDecentralizedPowerPlant(WaterEnergyEntity WaterEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    void assignToHousehold(WaterEnergyEntity entity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    void deleteById(WaterEnergyIdVO id) throws ProducerRepositoryException;

    void update(WaterEnergyIdVO id, WaterEnergyEntity domainEntity) throws ProducerRepositoryException;
}
