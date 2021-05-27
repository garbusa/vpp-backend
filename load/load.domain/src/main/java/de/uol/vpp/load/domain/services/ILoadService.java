package de.uol.vpp.load.domain.services;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für das Lastaggregat-Service in der Serviceschicht
 */
public interface ILoadService {
    /**
     * Geschäftslogik für das Holen der Lastaggregate einer Maßnahmenabfrage
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Liste der Lastaggregate
     * @throws LoadServiceException e
     */
    List<LoadAggregate> getLoadsByActionRequestId(String actionRequestId) throws LoadServiceException;
}
