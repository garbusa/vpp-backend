package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerJpaRepository extends JpaRepository<Consumer, Long> {
}
