package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.OtherEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtherEnergyJpaRepository extends JpaRepository<OtherEnergy, Long> {
    Optional<OtherEnergy> findOneById(String otherEnergyId);

    List<OtherEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    List<OtherEnergy> findAllByHousehold(Household household);
}
