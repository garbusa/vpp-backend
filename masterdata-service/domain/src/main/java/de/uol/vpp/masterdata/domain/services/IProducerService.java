package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.ProducerEntity;

import java.util.List;

public interface IProducerService {
    List<ProducerEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws ProducerServiceException;

    List<ProducerEntity> getAllByHouseholdId(String householdBusinessKey) throws ProducerServiceException;

    ProducerEntity get(String businessKey) throws ProducerServiceException;

    void saveWithDecentralizedPowerPlant(ProducerEntity domainEntity, String dppBusinessKey) throws ProducerServiceException;

    void saveWithHousehold(ProducerEntity domainEntity, String householdBusinessKey) throws ProducerServiceException;

    void delete(String businessKey, String vppBusinessKey) throws ProducerServiceException;

    void updateStatus(String businessKey, Double capacity, boolean running, String vppBusinessKey) throws ProducerServiceException;

    void update(String businessKey, ProducerEntity toDomain, String vppBusinessKey) throws ProducerServiceException;
}
