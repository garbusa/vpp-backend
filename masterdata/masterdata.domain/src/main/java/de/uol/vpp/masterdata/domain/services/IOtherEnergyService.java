package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für den Service von alternativen Erzeugungsanlagen in der Serviceschicht
 */
public interface IOtherEnergyService {
    /**
     * Holt alle alternativen Erzeugungsanlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste alternativer Erzeugungsanlagen
     * @throws ProducerServiceException e
     */
    List<OtherEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Holt alle alternativen Erzeugungsanlagen eines DK
     *
     * @param householdId Id des Haushalts
     * @return Liste alternativer Erzeugungsanlagen
     * @throws ProducerServiceException e
     */
    List<OtherEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException;

    /**
     * Holt eine spezifische alternative Erzeugungsanlage
     *
     * @param otherEnergyId Id der alternativen Erzeugungsanlage
     * @return alternative Erzeugungsanlage
     * @throws ProducerServiceException e
     */
    OtherEnergyEntity get(String otherEnergyId) throws ProducerServiceException;

    /**
     * Persistiert alternative Erzeugungsanlage und weist es einem DK zu
     *
     * @param domainEntity              alternative Erzeugungsanlage
     * @param decentralizedPowerPlantId Id des DK
     * @throws ProducerServiceException e
     */
    void saveWithDecentralizedPowerPlant(OtherEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException;

    /**
     * Persistiert alternative Erzeugungsanlage und weist es einem Haushalt zu
     *
     * @param domainEntity alternative Erzeugungsanlage
     * @param householdId  Id des Haushalt
     * @throws ProducerServiceException e
     */
    void saveWithHousehold(OtherEnergyEntity domainEntity, String householdId) throws ProducerServiceException;

    /**
     * Löscht eine alternative Erzeugungsanlage
     *
     * @param otherEnergyId       Id der alternativen Erzeugungsanlage
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void delete(String otherEnergyId, String virtualPowerPlantId) throws ProducerServiceException;

    /**
     * Aktualisiert eine alternative Erzeugungsanlage
     *
     * @param otherEnergyId       Id der alternativen Erzeugungsanlage
     * @param domainEntity        aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @throws ProducerServiceException e
     */
    void update(String otherEnergyId, OtherEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException;
}
