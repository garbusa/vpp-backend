package de.uol.vpp.load.infrastructure.jpaRepositories;

import de.uol.vpp.load.infrastructure.entities.ELoad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring-JPA Repository für die Persistierung von Lastaggregate
 */
public interface LoadJpaRepository extends JpaRepository<ELoad, ELoad.ActionRequestTimestamp> {
    /**
     * Hole alle Lastaggegate einer Maßnahmenabfrage (List<ELoad> -> Tagesprognose)
     *
     * @param actionRequestId Maßnahmenabfrage
     * @return Liste der Lastaggregate
     */
    List<ELoad> findAllByActionRequestTimestamp_ActionRequestId(String actionRequestId);
}
