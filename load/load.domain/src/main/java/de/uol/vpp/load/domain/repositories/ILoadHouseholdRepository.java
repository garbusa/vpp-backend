package de.uol.vpp.load.domain.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadHouseholdRepositoryException;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;

import java.util.List;

public interface ILoadHouseholdRepository {
    List<LoadHouseholdEntity> getLoadHouseholdsByVppTimestamp(LoadVirtualPowerPlantIdVO vppBusinessKey, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException;

    void assign(LoadHouseholdEntity loadHousehold, LoadAggregate load) throws LoadHouseholdRepositoryException;

    void saveLoadHousehold(LoadHouseholdEntity load) throws LoadHouseholdRepositoryException;

    void deleteHouseholdsByVppTimestamp(LoadVirtualPowerPlantIdVO vppBusinessKey, LoadStartTimestampVO timestamp) throws LoadHouseholdRepositoryException;
}
