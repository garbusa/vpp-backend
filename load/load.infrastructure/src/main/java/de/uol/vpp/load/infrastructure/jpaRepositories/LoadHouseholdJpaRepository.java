package de.uol.vpp.load.infrastructure.jpaRepositories;

import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.entities.ELoadHousehold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring-JPA Repository für die Persistierung von Haushaltslasten
 */
public interface LoadHouseholdJpaRepository extends JpaRepository<ELoadHousehold, Long> {
    /**
     * Hole alle Haushaltslasten mittels dem Lastaggregat
     *
     * @param load Lastaggregat
     * @return Liste der Haushaltslasten
     */
    List<ELoadHousehold> findAllByLoad(ELoad load);

    /**
     * Lösche alle Haushaltslasten, die dem Lastaggregat angehören
     *
     * @param load Lastaggregat
     */
    void deleteAllByLoad(ELoad load);
}
