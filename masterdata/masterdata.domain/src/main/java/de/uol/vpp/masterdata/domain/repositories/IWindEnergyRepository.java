package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.WindEnergyEntity;
import de.uol.vpp.masterdata.domain.valueobjects.WindEnergyIdVO;

import java.util.List;
import java.util.Optional;

public interface IWindEnergyRepository {
    List<WindEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    List<WindEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    Optional<WindEnergyEntity> getById(WindEnergyIdVO id) throws ProducerRepositoryException;

    void save(WindEnergyEntity WindEnergyEntity) throws ProducerRepositoryException;

    void assignToDecentralizedPowerPlant(WindEnergyEntity WindEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    void assignToHousehold(WindEnergyEntity entity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    void deleteById(WindEnergyIdVO id) throws ProducerRepositoryException;

    void update(WindEnergyIdVO id, WindEnergyEntity domainEntity) throws ProducerRepositoryException;
}
