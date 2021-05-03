package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;

import java.util.List;

public interface IVirtualPowerPlantService {
    List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantServiceException;

    List<VirtualPowerPlantAggregate> getAllActives() throws VirtualPowerPlantServiceException;

    VirtualPowerPlantAggregate get(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException;

    void delete(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    void publish(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    void unpublish(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    void update(String virtualPowerPlantId, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException;
}
