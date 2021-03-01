package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;

import java.util.List;

public interface IVirtualPowerPlantService {
    List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantServiceException;

    VirtualPowerPlantAggregate get(String businessKey) throws VirtualPowerPlantServiceException;

    void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException;

    void delete(String businessKey) throws VirtualPowerPlantServiceException;

    void publish(String businessKey) throws VirtualPowerPlantServiceException;

    void unpublish(String businessKey) throws VirtualPowerPlantServiceException;

    void update(String businessKey, VirtualPowerPlantAggregate toDomain) throws VirtualPowerPlantServiceException;
}
