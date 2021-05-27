package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.WindEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository der Windkraftanlagen für die Kommunikation mit der Datenbank
 */
public interface WindEnergyJpaRepository extends JpaRepository<WindEnergy, Long> {
    /**
     * Holt spezifische Windkraftanlage
     *
     * @param windEnergyId Id der Windkraftanlage
     * @return Windkraftanlage
     */
    Optional<WindEnergy> findOneById(String windEnergyId);

    /**
     * Holt alle Windkraftanlagen eines DK
     *
     * @param decentralizedPowerPlant DK
     * @return Liste von Windkraftanlagen
     */
    List<WindEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    /**
     * Holt alle Windkraftanlagen eines Haushalts
     *
     * @param household Haushalt
     * @return Liste von Windkraftanlagen
     */
    List<WindEnergy> findAllByHousehold(Household household);
}
