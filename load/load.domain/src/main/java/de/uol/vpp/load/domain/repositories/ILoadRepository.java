package de.uol.vpp.load.domain.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;

import java.util.List;

/**
 * Schnittstellendefinition für das Lastaggregat-Repository in der Infrastrukturenschicht
 */
public interface ILoadRepository {
    /**
     * Holt alle Lastaggregate pro Zeitstempel einer Maßnahmenabfrage
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Liste aller Lastaggregate
     * @throws LoadRepositoryException e
     */
    List<LoadAggregate> getLoadsByActionRequestId(LoadActionRequestIdVO actionRequestId) throws LoadRepositoryException;

    /**
     * Persistiert ein Lastaggregat
     *
     * @param load Lastaggregat
     * @throws LoadRepositoryException
     */
    void saveLoad(LoadAggregate load) throws LoadRepositoryException;
}
