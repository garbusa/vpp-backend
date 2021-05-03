package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;

import java.util.List;

public interface IWaterEnergyService {
    List<WaterEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    List<WaterEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    WaterEnergyEntity get(String waterEnergyId) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(WaterEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    void saveWithHousehold(WaterEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    void delete(String waterEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    void update(String waterEnergyId, WaterEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
