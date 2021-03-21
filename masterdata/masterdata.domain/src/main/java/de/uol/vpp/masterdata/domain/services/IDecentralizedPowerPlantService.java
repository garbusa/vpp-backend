package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;

import java.util.List;

public interface IDecentralizedPowerPlantService {
    List<DecentralizedPowerPlantAggregate> getAllByVppId(String vppBusinessKey) throws DecentralizedPowerPlantServiceException;

    DecentralizedPowerPlantAggregate get(String businessKey) throws DecentralizedPowerPlantServiceException;

    void save(DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantBusinessKey) throws DecentralizedPowerPlantServiceException;

    void delete(String businessKey, String vppBusinessKey) throws DecentralizedPowerPlantServiceException;

    void update(String businessKey, DecentralizedPowerPlantAggregate toDomain, String vppBusinessKey) throws DecentralizedPowerPlantServiceException;
}
