package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das VK-Repository
 */
public interface IVirtualPowerPlantRepository {
    /**
     * Holt alle VK
     *
     * @return Liste von VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantRepositoryException;

    /**
     * Holt ein spezifisches VK
     *
     * @param id Id des VK
     * @return VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    Optional<VirtualPowerPlantAggregate> getById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    /**
     * Holt ein VK durch DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    VirtualPowerPlantAggregate getByDpp(DecentralizedPowerPlantIdVO decentralizedPowerPlantId) throws VirtualPowerPlantRepositoryException;

    /**
     * Holt ein VK durch Haushalt
     *
     * @param householdId Id des Haushalts
     * @return VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    VirtualPowerPlantAggregate getByHousehold(HouseholdIdVO householdId) throws VirtualPowerPlantRepositoryException;

    /**
     * Persistiert ein VK
     *
     * @param entity VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    void save(VirtualPowerPlantAggregate entity) throws VirtualPowerPlantRepositoryException;

    /**
     * Löscht ein VK
     *
     * @param id Id des VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    void deleteById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    /**
     * Veröffentlicht ein VK
     *
     * @param id Id des VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    void publish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    /**
     * Macht die Veröffentlichung des VK rückgängig
     *
     * @param id Id des VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    void unpublish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    /**
     * Prüft, ob ein VK veröffentlicht ist
     *
     * @param id Id des Vk
     * @return true/false
     * @throws VirtualPowerPlantRepositoryException e
     */
    boolean isPublished(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException;

    /**
     * Aktualisiert ein VK
     *
     * @param id           id des VK
     * @param domainEntity aktualisierte Daten des VK
     * @throws VirtualPowerPlantRepositoryException e
     */
    void update(VirtualPowerPlantIdVO id, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantRepositoryException;
}
