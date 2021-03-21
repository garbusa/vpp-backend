package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.Producer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProducerJpaRepository extends JpaRepository<Producer, Long> {
    Optional<Producer> findOneByBusinessKey(String businessKey);

    List<Producer> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    List<Producer> findAllByHousehold(Household household);
}
