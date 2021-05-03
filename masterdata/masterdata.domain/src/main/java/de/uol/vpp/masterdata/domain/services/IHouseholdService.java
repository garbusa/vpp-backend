package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;

import java.util.List;

public interface IHouseholdService {
    List<HouseholdAggregate> getAllByVppId(String virtualPowerPlantId) throws HouseholdServiceException;

    HouseholdAggregate get(String householdId) throws HouseholdServiceException;

    void save(HouseholdAggregate domainEntity, String virtualPowerPlantId) throws HouseholdServiceException;

    void delete(String householdId, String virtualPowerPlantId) throws HouseholdServiceException;

    void update(String householdId, HouseholdAggregate domainEntity, String virtualPowerPlantId) throws HouseholdServiceException;
}
