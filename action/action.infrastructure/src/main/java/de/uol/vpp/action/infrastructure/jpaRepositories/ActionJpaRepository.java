package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionJpaRepository extends JpaRepository<Action, Action.ActionPrimaryKey> {
}
