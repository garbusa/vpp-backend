package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.WaterEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaterEnergyJpaRepository extends JpaRepository<WaterEnergy, Long> {
    Optional<WaterEnergy> findOneByBusinessKey(String businessKey);

    List<WaterEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    List<WaterEnergy> findAllByHousehold(Household household);
}
