package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;

import java.util.List;

public interface IHouseholdService {
    List<HouseholdAggregate> getAllByVppId(String vppBusinessKey) throws HouseholdServiceException;

    HouseholdAggregate get(String businessKey) throws HouseholdServiceException;

    void save(HouseholdAggregate domainEntity, String vppBusinessKey) throws HouseholdServiceException;

    void delete(String businessKey, String vppBusinessKey) throws HouseholdServiceException;

    void update(String businessKey, HouseholdAggregate toDomain, String vppBusinessKey) throws HouseholdServiceException;
}
