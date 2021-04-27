package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.ActionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionRequestJpaRepository extends JpaRepository<ActionRequest, String> {
    List<ActionRequest> findAllByVirtualPowerPlantId(String virtualPowerPlantId);
}
