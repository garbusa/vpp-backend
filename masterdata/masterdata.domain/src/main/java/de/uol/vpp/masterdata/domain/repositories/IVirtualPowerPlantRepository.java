package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;

import java.util.List;
import java.util.Optional;

public interface IVirtualPowerPlantRepository {
    List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantRepositoryException;

    Optional<VirtualPowerPlantAggregate> getById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    VirtualPowerPlantAggregate getByDpp(DecentralizedPowerPlantIdVO decentralizedPowerPlantId) throws VirtualPowerPlantRepositoryException;

    VirtualPowerPlantAggregate getByHousehold(HouseholdIdVO householdId) throws VirtualPowerPlantRepositoryException;

    void save(VirtualPowerPlantAggregate entity) throws VirtualPowerPlantRepositoryException;

    void deleteById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    void publish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    void unpublish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    boolean isPublished(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    void update(VirtualPowerPlantIdVO id, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantRepositoryException;
}
