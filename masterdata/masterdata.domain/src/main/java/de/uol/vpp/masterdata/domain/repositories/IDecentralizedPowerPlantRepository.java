package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das DK-Repository
 */
public interface IDecentralizedPowerPlantRepository {
    /**
     * Holt alle dezentrale Kraftwerke eines VK
     *
     * @param virtualPowerPlantAggregate VK
     * @return Liste von DK
     * @throws VirtualPowerPlantRepositoryException       e
     * @throws DecentralizedPowerPlantRepositoryException e
     */
    List<DecentralizedPowerPlantAggregate> getAllByVppId(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws VirtualPowerPlantRepositoryException, DecentralizedPowerPlantRepositoryException;

    /**
     * Holt ein DK
     *
     * @param id Id des DK
     * @return DK
     * @throws DecentralizedPowerPlantRepositoryException e
     */
    Optional<DecentralizedPowerPlantAggregate> getById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException;

    /**
     * Persistiert ein DK
     *
     * @param entity DK
     * @throws DecentralizedPowerPlantRepositoryException e
     */
    void save(DecentralizedPowerPlantAggregate entity) throws DecentralizedPowerPlantRepositoryException;

    /**
     * Löscht ein DK
     *
     * @param id Id des DK
     * @throws DecentralizedPowerPlantRepositoryException e
     */
    void deleteById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException;

    /**
     * Weist DK einem VK zu
     *
     * @param entity            DK
     * @param virtualPowerPlant VK
     * @throws DecentralizedPowerPlantRepositoryException e
     */
    void assign(DecentralizedPowerPlantAggregate entity, VirtualPowerPlantAggregate virtualPowerPlant) throws DecentralizedPowerPlantRepositoryException;

    /**
     * Aktualisiert ein DK
     *
     * @param id           Id des DK
     * @param domainEntity aktualisierte Daten
     * @throws DecentralizedPowerPlantRepositoryException e
     */
    void update(DecentralizedPowerPlantIdVO id, DecentralizedPowerPlantAggregate domainEntity) throws DecentralizedPowerPlantRepositoryException;
}
