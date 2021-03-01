package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;

import java.util.List;
import java.util.Optional;

public interface IHouseholdRepository {
    List<HouseholdAggregate> getAllByVirtualPowerPlant(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws HouseholdRepositoryException;

    Optional<HouseholdAggregate> getById(HouseholdIdVO id) throws HouseholdRepositoryException;

    void save(HouseholdAggregate entity) throws HouseholdRepositoryException;

    void deleteById(HouseholdIdVO id) throws HouseholdRepositoryException;

    void assign(HouseholdAggregate entity, VirtualPowerPlantAggregate virtualPowerPlant) throws HouseholdRepositoryException;

    void update(HouseholdIdVO id, HouseholdAggregate domainEntity) throws HouseholdRepositoryException;
}
