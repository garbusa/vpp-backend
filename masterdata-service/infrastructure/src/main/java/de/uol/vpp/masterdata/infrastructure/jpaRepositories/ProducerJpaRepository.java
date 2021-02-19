package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.Producer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerJpaRepository extends JpaRepository<Producer, Long> {
}
