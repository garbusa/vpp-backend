package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantServiceException;

import java.util.List;

/**
 * Schnittstellendefinition des DK-Service für die Serviceschicht
 */
public interface IDecentralizedPowerPlantService {
    /**
     * Holt alle DK eines VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste von DK
     * @throws DecentralizedPowerPlantServiceException e
     */
    List<DecentralizedPowerPlantAggregate> getAllByVppId(String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;

    /**
     * Holt ein DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return DK
     * @throws DecentralizedPowerPlantServiceException e
     */
    DecentralizedPowerPlantAggregate get(String decentralizedPowerPlantId) throws DecentralizedPowerPlantServiceException;

    /**
     * Persistiert ein DK
     *
     * @param domainEntity        DK
     * @param virtualPowerPlantId Id des VK
     * @throws DecentralizedPowerPlantServiceException e
     */
    void save(DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;

    /**
     * Löscht ein DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @param virtualPowerPlantId       Id des VK
     * @throws DecentralizedPowerPlantServiceException e
     */
    void delete(String decentralizedPowerPlantId, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;

    /**
     * Aktualisiert ein DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @param domainEntity              aktualisierte Daten
     * @param virtualPowerPlantId       Id des VK
     * @throws DecentralizedPowerPlantServiceException e
     */
    void update(String decentralizedPowerPlantId, DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException;
}
