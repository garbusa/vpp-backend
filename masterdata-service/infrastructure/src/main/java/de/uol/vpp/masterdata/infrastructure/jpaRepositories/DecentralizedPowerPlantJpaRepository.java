package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DecentralizedPowerPlantJpaRepository extends JpaRepository<DecentralizedPowerPlant, Long> {
    Optional<DecentralizedPowerPlant> findOneByBusinessKey(String businessKey);

    List<DecentralizedPowerPlant> findAllByVirtualPowerPlant(VirtualPowerPlant virtualPowerPlant);
}
