package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;

import java.util.List;

public interface IOtherEnergyService {
    List<OtherEnergyEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws ProducerServiceException;

    List<OtherEnergyEntity> getAllByHouseholdId(String householdBusinessKey) throws ProducerServiceException;

    OtherEnergyEntity get(String businessKey) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(OtherEnergyEntity domainEntity, String dppBusinessKey) throws ProducerServiceException;

    void saveWithHousehold(OtherEnergyEntity domainEntity, String householdBusinessKey) throws ProducerServiceException;

    void delete(String businessKey, String vppBusinessKey) throws ProducerServiceException;

    void update(String businessKey, OtherEnergyEntity toDomain, String vppBusinessKey) throws ProducerServiceException;
}
