package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.OtherEnergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository der alternativen Erzeugungsanlagen für die Kommunikation mit der Datenbank
 */
public interface OtherEnergyJpaRepository extends JpaRepository<OtherEnergy, Long> {
    /**
     * Holt eine spezifische alternative Erzeugungsanlage
     *
     * @param otherEnergyId Id der alternativen Erzeugungsanlage
     * @return alternative Erzeugungsanlage
     */
    Optional<OtherEnergy> findOneById(String otherEnergyId);

    /**
     * Holt alle alternativen Erzeugungsanlagen eines DK
     *
     * @param decentralizedPowerPlant DK
     * @return Liste von alternativen Erzeugungsanlagen
     */
    List<OtherEnergy> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    /**
     * Holt alle alternativen Erzeugungsanlagen eines Haushalts
     *
     * @param household Haushalt
     * @return Liste von alternativen Erzeugungsanlagen
     */
    List<OtherEnergy> findAllByHousehold(Household household);
}
