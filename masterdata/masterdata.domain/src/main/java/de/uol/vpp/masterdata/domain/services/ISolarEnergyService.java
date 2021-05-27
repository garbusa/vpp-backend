package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für den Service von Solaranlagen in der Serviceschicht
 */
public interface ISolarEnergyService {
    /**
     * Holt alle Solaranlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste von Solaranlagen
     * @throws ProducerServiceException e
     */
    List<SolarEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Holt alle Solaranlagen eines DK
     *
     * @param householdId Id des Haushalts
     * @return Liste von Solaranlagen
     * @throws ProducerServiceException e
     */
    List<SolarEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    /**
     * Holt eine spezifische Solaranlage
     *
     * @param solarEnergyId Id der Solaranlage
     * @return Solaranlage
     * @throws ProducerServiceException e
     */
    SolarEnergyEntity get(String solarEnergyId) throws ProducerServiceException;

    /**
     * Persistiert Solaranlage und weist es einem DK zu
     *
     * @param domainEntity              Solaranlage
     * @param decentralizedPowerPlantId Id des DK
     * @throws ProducerServiceException e
     */
    void saveWithDecentralizedPowerPlant(SolarEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Persistiert Solaranlage und weist es einem Haushalt zu
     *
     * @param domainEntity Solaranlage
     * @param householdId  Id des Haushalt
     * @throws ProducerServiceException e
     */
    void saveWithHousehold(SolarEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    /**
     * Löscht eine Solaranlage
     *
     * @param solarEnergyId       Id der Solaranlage
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void delete(String solarEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    /**
     * Aktualisiert eine Solaranlage
     *
     * @param solarEnergyId       Id der Solaranlage
     * @param domainEntity        aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void update(String solarEnergyId, SolarEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
