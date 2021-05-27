package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.ActionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring-JPA Repository für die Persistierung von Maßnahmenabfragen
 */
public interface ActionRequestJpaRepository extends JpaRepository<ActionRequest, String> {
    /**
     * Hole alle Maßnahmenabfrage mittels Id eines VK
     *
     * @param virtualPowerPlantId Id eines VK
     * @return Liste der dazugehörigen Maßnahmenabfrage
     */
    List<ActionRequest> findAllByVirtualPowerPlantId(String virtualPowerPlantId);
}
