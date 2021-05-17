package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.ProducerManipulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerManipulationJpaRepository extends JpaRepository<ProducerManipulation, ProducerManipulation.ProducerManipulationPrimaryKey> {
}
