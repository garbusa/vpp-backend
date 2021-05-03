package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity;

import java.util.List;

public interface ISolarEnergyService {
    List<SolarEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    List<SolarEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    SolarEnergyEntity get(String solarEnergyId) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(SolarEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    void saveWithHousehold(SolarEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    void delete(String solarEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    void update(String solarEnergyId, SolarEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
