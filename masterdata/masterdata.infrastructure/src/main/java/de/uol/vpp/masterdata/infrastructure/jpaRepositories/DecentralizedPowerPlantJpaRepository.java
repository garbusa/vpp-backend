package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * DK JPA Repository für die Kommunikation mit der Datenbank
 */
public interface DecentralizedPowerPlantJpaRepository extends JpaRepository<DecentralizedPowerPlant, Long> {
    /**
     * Hole spezifische DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return DK
     */
    Optional<DecentralizedPowerPlant> findOneById(String decentralizedPowerPlantId);

    /**
     * Hole alle DK eines VK
     *
     * @param virtualPowerPlant VK
     * @return Liste von DK
     */
    List<DecentralizedPowerPlant> findAllByVirtualPowerPlant(VirtualPowerPlant virtualPowerPlant);
}
