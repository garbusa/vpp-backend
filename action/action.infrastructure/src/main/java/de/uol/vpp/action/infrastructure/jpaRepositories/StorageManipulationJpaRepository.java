package de.uol.vpp.action.infrastructure.jpaRepositories;

import de.uol.vpp.action.infrastructure.entities.StorageManipulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageManipulationJpaRepository extends JpaRepository<StorageManipulation, StorageManipulation.StorageManipulationPrimaryKey> {
}
