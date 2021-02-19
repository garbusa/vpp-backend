package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageJpaRepository extends JpaRepository<Storage, Long> {
}
