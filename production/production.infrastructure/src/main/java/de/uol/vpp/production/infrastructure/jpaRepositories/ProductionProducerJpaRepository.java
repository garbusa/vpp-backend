package de.uol.vpp.production.infrastructure.jpaRepositories;

import de.uol.vpp.production.infrastructure.entities.ProductionProducer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Erzeugungswert JPA Repository für die Interaktion mit der Datenbank
 */
public interface ProductionProducerJpaRepository extends JpaRepository<ProductionProducer, Long> {
}
