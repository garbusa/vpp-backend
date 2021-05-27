package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring-JPA Repository für die Persistierung von Handlungsempfehlungen
 */
public interface ActionJpaRepository extends JpaRepository<Action, Action.ActionPrimaryKey> {
}
