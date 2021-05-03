package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.WindEnergyEntity;

import java.util.List;

public interface IWindEnergyService {
    List<WindEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    List<WindEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    WindEnergyEntity get(String windEnergyId) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(WindEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    void saveWithHousehold(WindEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    void delete(String windEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    void update(String windEnergyId, WindEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
