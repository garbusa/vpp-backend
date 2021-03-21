package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VirtualPowerPlantJpaRepository extends JpaRepository<VirtualPowerPlant, Long> {
    Optional<VirtualPowerPlant> findOneByBusinessKey(String businessKey);
}
