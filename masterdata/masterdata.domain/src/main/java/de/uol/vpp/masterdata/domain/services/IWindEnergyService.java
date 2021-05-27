package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.WindEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für den Service von Windkraftanlagen in der Serviceschicht
 */
public interface IWindEnergyService {
    /**
     * Holt alle Windkraftanlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste von Windkraftanlagen
     * @throws ProducerServiceException e
     */
    List<WindEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Holt alle Windkraftanlagen eines DK
     *
     * @param householdId Id des Haushalts
     * @return Liste von Windkraftanlagen
     * @throws ProducerServiceException e
     */
    List<WindEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    /**
     * Holt eine spezifische Windkraftanlage
     *
     * @param windEnergyId Id der Windkraftanlage
     * @return Windkraftanlage
     * @throws ProducerServiceException e
     */
    WindEnergyEntity get(String windEnergyId) throws ProducerServiceException;

    /**
     * Persistiert Windkraftanlage und weist es einem DK zu
     *
     * @param domainEntity              Windkraftanlage
     * @param decentralizedPowerPlantId Id des DK
     * @throws ProducerServiceException e
     */
    void saveWithDecentralizedPowerPlant(WindEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Persistiert Windkraftanlage und weist es einem Haushalt zu
     *
     * @param domainEntity Windkraftanlage
     * @param householdId  Id des Haushalt
     * @throws ProducerServiceException e
     */
    void saveWithHousehold(WindEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    /**
     * Löscht eine Windkraftanlage
     *
     * @param windEnergyId        Id der Windkraftanlage
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void delete(String windEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    /**
     * Aktualisiert eine Windkraftanlage
     *
     * @param windEnergyId        Id der Windkraftanlage
     * @param domainEntity        aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void update(String windEnergyId, WindEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
