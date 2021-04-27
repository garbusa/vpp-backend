package de.uol.vpp.load.infrastructure.jpaRepositories;

import de.uol.vpp.load.infrastructure.entities.ELoad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoadJpaRepository extends JpaRepository<ELoad, ELoad.ActionRequestTimestamp> {
    List<ELoad> findAllByActionRequestTimestamp_ActionRequestId(String actionRequestId);
}
