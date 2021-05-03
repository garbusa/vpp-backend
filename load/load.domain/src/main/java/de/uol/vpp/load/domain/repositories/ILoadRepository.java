package de.uol.vpp.load.domain.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;

import java.util.List;

public interface ILoadRepository {
    List<LoadAggregate> getLoadsByActionRequestId(LoadActionRequestIdVO actionRequestId) throws LoadRepositoryException;

    void saveLoad(LoadAggregate load) throws LoadRepositoryException;

    void deleteLoadsByActionRequestId(LoadActionRequestIdVO actionRequestId) throws LoadRepositoryException;

    void updateLoad(LoadAggregate load) throws LoadRepositoryException;
}
