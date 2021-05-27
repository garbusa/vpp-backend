package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.SolarEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository der Solaranlagen für die Kommunikation mit der Datenbank
 */
public interface SolarEnergyJpaRepository extends JpaRepository<SolarEnergy, Long> {
    /**
     * Holt spezifische Solaranlage
     *
     * @param solarEnergyId Id der Solaranlage
     * @return Solaranlage
     */
    Optional<SolarEnergy> findOneById(String solarEnergyId);

    /**
     * Holt alle Solaranlagen eines DK
     *
     * @param decentralizedPowerPlant DK
     * @return Liste von Solaranlagen
     */
    List<SolarEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    /**
     * Holt alle Solaranlagen eines Haushalts
     *
     * @param household Haushalt
     * @return Liste von Solaranlagen
     */
    List<SolarEnergy> findAllByHousehold(Household household);
}
