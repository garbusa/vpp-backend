package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;

import java.util.List;

public interface IDecentralizedPowerPlantService {
    List<DecentralizedPowerPlantAggregate> getAllByVppId(String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;

    DecentralizedPowerPlantAggregate get(String decentralizedPowerPlantId) throws DecentralizedPowerPlantServiceException;

    void save(DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;

    void delete(String decentralizedPowerPlantId, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;

    void update(String decentralizedPowerPlantId, DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;
}
