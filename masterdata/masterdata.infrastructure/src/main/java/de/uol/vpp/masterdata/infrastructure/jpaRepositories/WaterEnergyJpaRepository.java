package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.WaterEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository der Wasserkraftanlagen für die Kommunikation mit der Datenbank
 */
public interface WaterEnergyJpaRepository extends JpaRepository<WaterEnergy, Long> {
    /**
     * Holt spezifische Wasserkraftanlage
     *
     * @param waterEnergyId Id der Wasserkraftanlage
     * @return Wasserkraftanlage
     */
    Optional<WaterEnergy> findOneById(String waterEnergyId);

    /**
     * Holt alle Wasserkraftanlagen eines DK
     *
     * @param decentralizedPowerPlant DK
     * @return Liste von Wasserkraftanlagen
     */
    List<WaterEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    /**
     * Holt alle Wasserkraftanlagen eines Haushalts
     *
     * @param household Haushalt
     * @return Liste von Wasserkraftanlagen
     */
    List<WaterEnergy> findAllByHousehold(Household household);
}
