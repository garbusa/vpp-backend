package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.ActionCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring-JPA Repository für die Persistierung von Handlungsempfehlungekatalogen
 */
public interface ActionCatalogJpaRepository extends JpaRepository<ActionCatalog, ActionCatalog.ActionCatalogPrimaryKey> {
}
