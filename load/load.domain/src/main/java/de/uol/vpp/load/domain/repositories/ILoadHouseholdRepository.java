package de.uol.vpp.load.domain.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadHouseholdRepositoryException;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;

import java.util.List;

public interface ILoadHouseholdRepository {
    List<LoadHouseholdEntity> getLoadHouseholdByActionRequestId(LoadActionRequestIdVO actionRequestId, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException;

    void assignToInternal(Long loadHouseholdInternalId, LoadAggregate load) throws LoadHouseholdRepositoryException;

    Long saveLoadHouseholdInternal(LoadHouseholdEntity load) throws LoadHouseholdRepositoryException;

    void deleteHouseholdsByActionRequestId(LoadActionRequestIdVO actionRequestId, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException;
}
