package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyIdVO;

import java.util.List;
import java.util.Optional;

public interface IOtherEnergyRepository {
    List<OtherEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    List<OtherEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    Optional<OtherEnergyEntity> getById(OtherEnergyIdVO id) throws ProducerRepositoryException;

    void save(OtherEnergyEntity domainEntity) throws ProducerRepositoryException;

    void assignToDecentralizedPowerPlant(OtherEnergyEntity domainEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    void assignToHousehold(OtherEnergyEntity domainEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    void deleteById(OtherEnergyIdVO id) throws ProducerRepositoryException;

    void update(OtherEnergyIdVO id, OtherEnergyEntity domainEntity) throws ProducerRepositoryException;
}
