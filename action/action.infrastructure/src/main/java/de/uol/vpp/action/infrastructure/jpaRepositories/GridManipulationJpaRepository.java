package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.GridManipulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GridManipulationJpaRepository extends JpaRepository<GridManipulation, GridManipulation.GridManipulationPrimaryKey> {
}
