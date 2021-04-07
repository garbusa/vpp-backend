package de.uol.vpp.load.domain.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;

import java.time.ZonedDateTime;
import java.util.List;

public interface ILoadRepository {
    List<LoadAggregate> getCurrentVppLoads(LoadVirtualPowerPlantIdVO vppBusinessKey) throws LoadRepositoryException;

    List<LoadAggregate> getForecastedVppLoads(LoadVirtualPowerPlantIdVO vppBusinessKey) throws LoadRepositoryException;

    void saveLoad(LoadAggregate load) throws LoadRepositoryException;

    void outdateLoad(LoadVirtualPowerPlantIdVO vppBusinessKey, ZonedDateTime compare) throws LoadRepositoryException;

    void deleteLoadsByVppId(LoadVirtualPowerPlantIdVO vppBusinessKey) throws LoadRepositoryException;

    void updateLoad(LoadAggregate load) throws LoadRepositoryException;
}
