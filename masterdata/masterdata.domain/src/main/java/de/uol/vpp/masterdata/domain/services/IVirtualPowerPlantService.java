package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für das VK-Service der Serviceschicht
 */
public interface IVirtualPowerPlantService {
    /**
     * Holt alle VK
     *
     * @return Liste von VK
     * @throws VirtualPowerPlantServiceException e
     */
    List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantServiceException;

    /**
     * Holt alle veröffentlichten VK
     *
     * @return Liste veröffentlichter VK
     * @throws VirtualPowerPlantServiceException e
     */
    List<VirtualPowerPlantAggregate> getAllActives() throws VirtualPowerPlantServiceException;

    /**
     * Holt ein spezifisches VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return VK
     * @throws VirtualPowerPlantServiceException e
     */
    VirtualPowerPlantAggregate get(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    /**
     * Persistiert ein VK
     *
     * @param domainEntity VK
     * @throws VirtualPowerPlantServiceException e
     */
    void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException;

    /**
     * Löscht ein VK
     *
     * @param virtualPowerPlantId Id des VK
     * @throws VirtualPowerPlantServiceException e
     */
    void delete(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    /**
     * Veröffentlicht ein VK
     *
     * @param virtualPowerPlantId Id des VK
     * @throws VirtualPowerPlantServiceException e
     */
    void publish(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    /**
     * Macht die Veröffentlichung eines VK rückgängig
     *
     * @param virtualPowerPlantId Id des VK
     * @throws VirtualPowerPlantServiceException e
     */
    void unpublish(String virtualPowerPlantId) throws VirtualPowerPlantServiceException;

    /**
     * Aktualisiert ein VK
     *
     * @param virtualPowerPlantId Id des VK
     * @param domainEntity        aktualisierte Daten des VK
     * @throws VirtualPowerPlantServiceException e
     */
    void update(String virtualPowerPlantId, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException;
}
