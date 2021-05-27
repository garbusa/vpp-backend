package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für den Service von Wasserkraftanlagen in der Serviceschicht
 */
public interface IWaterEnergyService {
    /**
     * Holt alle Wasserkraftanlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste von Wasserkraftanlagen
     * @throws ProducerServiceException e
     */
    List<WaterEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Holt alle Wasserkraftanlagen eines DK
     *
     * @param householdId Id des Haushalts
     * @return Liste von Wasserkraftanlagen
     * @throws ProducerServiceException e
     */
    List<WaterEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    /**
     * Holt eine spezifische Wasserkraftanlage
     *
     * @param waterEnergyId Id der Wasserkraftanlage
     * @return Wasserkraftanlage
     * @throws ProducerServiceException e
     */
    WaterEnergyEntity get(String waterEnergyId) throws ProducerServiceException;

    /**
     * Persistiert Wasserkraftanlage und weist es einem DK zu
     *
     * @param domainEntity              Wasserkraftanlage
     * @param decentralizedPowerPlantId Id des DK
     * @throws ProducerServiceException e
     */
    void saveWithDecentralizedPowerPlant(WaterEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Persistiert Wasserkraftanlage und weist es einem Haushalt zu
     *
     * @param domainEntity Wasserkraftanlage
     * @param householdId  Id des Haushalt
     * @throws ProducerServiceException e
     */
    void saveWithHousehold(WaterEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    /**
     * Löscht eine Wasserkraftanlage
     *
     * @param waterEnergyId       Id der Wasserkraftanlage
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void delete(String waterEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    /**
     * Aktualisiert eine Wasserkraftanlage
     *
     * @param waterEnergyId       Id der Wasserkraftanlage
     * @param domainEntity        aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void update(String waterEnergyId, WaterEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
