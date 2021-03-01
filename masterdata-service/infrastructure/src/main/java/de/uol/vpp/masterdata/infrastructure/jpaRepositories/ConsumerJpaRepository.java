package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.Consumer;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsumerJpaRepository extends JpaRepository<Consumer, Long> {
    Optional<Consumer> findOneByBusinessKey(String businessKey);

    List<Consumer> findAllByHousehold(Household household);
}
