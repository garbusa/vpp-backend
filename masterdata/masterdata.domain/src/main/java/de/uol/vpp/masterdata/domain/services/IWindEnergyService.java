package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.WindEnergyEntity;

import java.util.List;

public interface IWindEnergyService {
    List<WindEnergyEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws ProducerServiceException;

    List<WindEnergyEntity> getAllByHouseholdId(String householdBusinessKey) throws ProducerServiceException;

    WindEnergyEntity get(String businessKey) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(WindEnergyEntity domainEntity, String dppBusinessKey) throws ProducerServiceException;

    void saveWithHousehold(WindEnergyEntity domainEntity, String householdBusinessKey) throws ProducerServiceException;

    void delete(String businessKey, String vppBusinessKey) throws ProducerServiceException;

    void update(String businessKey, WindEnergyEntity toDomain, String vppBusinessKey) throws ProducerServiceException;
}
