package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;

import java.util.List;

public interface IWaterEnergyService {
    List<WaterEnergyEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws ProducerServiceException;

    List<WaterEnergyEntity> getAllByHouseholdId(String householdBusinessKey) throws ProducerServiceException;

    WaterEnergyEntity get(String businessKey) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(WaterEnergyEntity domainEntity, String dppBusinessKey) throws ProducerServiceException;

    void saveWithHousehold(WaterEnergyEntity domainEntity, String householdBusinessKey) throws ProducerServiceException;

    void delete(String businessKey, String vppBusinessKey) throws ProducerServiceException;

    void update(String businessKey, WaterEnergyEntity toDomain, String vppBusinessKey) throws ProducerServiceException;
}
