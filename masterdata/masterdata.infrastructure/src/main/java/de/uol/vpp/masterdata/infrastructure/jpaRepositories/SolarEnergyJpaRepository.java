package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.SolarEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolarEnergyJpaRepository extends JpaRepository<SolarEnergy, Long> {
    Optional<SolarEnergy> findOneByBusinessKey(String businessKey);

    List<SolarEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    List<SolarEnergy> findAllByHousehold(Household household);
}
