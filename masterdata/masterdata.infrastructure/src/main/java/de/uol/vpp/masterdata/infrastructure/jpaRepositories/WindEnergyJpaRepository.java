package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.WindEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WindEnergyJpaRepository extends JpaRepository<WindEnergy, Long> {
    Optional<WindEnergy> findOneByBusinessKey(String businessKey);

    List<WindEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    List<WindEnergy> findAllByHousehold(Household household);
}
