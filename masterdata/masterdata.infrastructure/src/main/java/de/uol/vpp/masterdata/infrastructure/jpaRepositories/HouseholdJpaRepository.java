package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseholdJpaRepository extends JpaRepository<Household, Long> {
    Optional<Household> findOneByBusinessKey(String businessKey);

    List<Household> findAllByVirtualPowerPlant(VirtualPowerPlant virtualPowerPlant);
}
