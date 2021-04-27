package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.ActionCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionCatalogJpaRepository extends JpaRepository<ActionCatalog, ActionCatalog.ActionCatalogPrimaryKey> {
}
