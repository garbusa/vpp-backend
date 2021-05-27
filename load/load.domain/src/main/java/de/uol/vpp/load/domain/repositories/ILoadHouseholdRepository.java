package de.uol.vpp.load.domain.repositories;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadHouseholdRepositoryException;

/**
 * Schnittstellendefinition für das Haushaltslast-Repository in der Infrastrukturenschicht
 */
public interface ILoadHouseholdRepository {
    /**
     * Weist ein Domänen-Objekt zur Datenbank-Entität, da die Datenbank-Entität eine von der Datenbank erstellten Id besitzt.
     *
     * @param loadHouseholdInternalId interne Datenbank Identifizierung
     * @param load                    Last Aggregat
     * @throws LoadHouseholdRepositoryException e
     */
    void assignToInternal(Long loadHouseholdInternalId, LoadAggregate load) throws LoadHouseholdRepositoryException;

    /**
     * Persistiert eine Haushaltslast
     *
     * @param load Haushaltslast
     * @return interne Datenbank Identifizierung
     * @throws LoadHouseholdRepositoryException e
     */
    Long saveLoadHouseholdInternal(LoadHouseholdEntity load) throws LoadHouseholdRepositoryException;
}
