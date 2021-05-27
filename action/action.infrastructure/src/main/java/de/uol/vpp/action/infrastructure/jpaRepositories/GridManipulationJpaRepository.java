package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.GridManipulation;
import de.uol.vpp.action.infrastructure.entities.embedded.ManipulationPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring-JPA Repository für die Persistierung von Stromnetzmanipulationen
 */
public interface GridManipulationJpaRepository extends JpaRepository<GridManipulation, ManipulationPrimaryKey> {
}
