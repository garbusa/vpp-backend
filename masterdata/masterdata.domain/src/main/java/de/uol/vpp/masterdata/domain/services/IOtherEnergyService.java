package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;

import java.util.List;

public interface IOtherEnergyService {
    List<OtherEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    List<OtherEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    OtherEnergyEntity get(String otherEnergyId) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(OtherEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    void saveWithHousehold(OtherEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    void delete(String otherEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    void update(String otherEnergyId, OtherEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
