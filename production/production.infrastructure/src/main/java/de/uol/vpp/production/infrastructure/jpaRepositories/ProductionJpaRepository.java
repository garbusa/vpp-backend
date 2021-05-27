package de.uol.vpp.production.infrastructure.jpaRepositories;

import de.uol.vpp.production.infrastructure.entities.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Erzeugungsaggregat JPA Repository für die Interaktion mit der Datenbank
 */
public interface ProductionJpaRepository extends JpaRepository<Production, Production.ActionRequestTimestamp> {
    /**
     * Holt alle Erzeugungsaggregate aus der Datenbank mittels Id der Maßnahmenabfrage
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Liste von Erzeugungsaggregaten
     */
    List<Production> findAllByActionRequestTimestamp_ActionRequestId(String actionRequestId);
}
