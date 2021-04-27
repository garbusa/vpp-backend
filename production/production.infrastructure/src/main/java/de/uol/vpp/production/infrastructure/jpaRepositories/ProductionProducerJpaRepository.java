package de.uol.vpp.production.infrastructure.jpaRepositories;

import de.uol.vpp.production.infrastructure.entities.Production;
import de.uol.vpp.production.infrastructure.entities.ProductionProducer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionProducerJpaRepository extends JpaRepository<ProductionProducer, Long> {
    List<ProductionProducer> findAllByProduction(Production production);

    void deleteAllByProduction(Production production);
}
