package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity;

import java.util.List;

public interface ISolarEnergyService {
    List<SolarEnergyEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws ProducerServiceException;

    List<SolarEnergyEntity> getAllByHouseholdId(String householdBusinessKey) throws ProducerServiceException;

    SolarEnergyEntity get(String businessKey) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(SolarEnergyEntity domainEntity, String dppBusinessKey) throws ProducerServiceException;

    void saveWithHousehold(SolarEnergyEntity domainEntity, String householdBusinessKey) throws ProducerServiceException;

    void delete(String businessKey, String vppBusinessKey) throws ProducerServiceException;

    void update(String businessKey, SolarEnergyEntity toDomain, String vppBusinessKey) throws ProducerServiceException;
}
