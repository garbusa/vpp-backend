package de.uol.vpp.production.infrastructure.jpaRepositories;

import de.uol.vpp.production.infrastructure.entities.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionJpaRepository extends JpaRepository<Production, Production.ActionRequestTimestamp> {
    List<Production> findAllByActionRequestTimestamp_ActionRequestId(String actionRequestId);
}
