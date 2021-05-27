package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Haushalt JPA Repository für die Kommunikation mit der Datenbank
 */
public interface HouseholdJpaRepository extends JpaRepository<Household, Long> {
    /**
     * Holt spezifisches Haushalt
     *
     * @param householdId Id des Haushalts
     * @return Haushalt
     */
    Optional<Household> findOneById(String householdId);

    /**
     * Holt alle Haushalte eines VK
     *
     * @param virtualPowerPlant Id des VK
     * @return Liste von Haushalten
     */
    List<Household> findAllByVirtualPowerPlant(VirtualPowerPlant virtualPowerPlant);
}
