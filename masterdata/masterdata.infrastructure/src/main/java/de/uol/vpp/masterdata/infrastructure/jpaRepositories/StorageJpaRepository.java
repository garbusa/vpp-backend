package de.uol.vpp.masterdata.infrastructure.jpaRepositories;

import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository der Speicheranlagen für die Kommunikation mit der Datenbank
 */
public interface StorageJpaRepository extends JpaRepository<Storage, Long> {
    /**
     * Holt spezifische Speicheranlage
     *
     * @param storageId Id der Speicheranlage
     * @return Speicheranlage
     */
    Optional<Storage> findOneById(String storageId);

    /**
     * Holt alle Speicheranlagen eines DK
     *
     * @param decentralizedPowerPlant DK
     * @return Liste von Speicheranlagen
     */
    List<Storage> findAllByDecentralizedPowerPlant(DecentralizedPowerPlant decentralizedPowerPlant);

    /**
     * Holt alle Speicheranlagen eines Haushalts
     *
     * @param household Haushalt
     * @return Liste von Speicheranlagen
     */
    List<Storage> findAllByHousehold(Household household);
}
