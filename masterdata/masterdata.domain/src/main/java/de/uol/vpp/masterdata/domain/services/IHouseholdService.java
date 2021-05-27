package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdServiceException;

import java.util.List;

/**
 * Schnittstellendefinition für den Haushalts-Service in der Serviceschicht
 */
public interface IHouseholdService {
    /**
     * Holt alle Haushalte eines VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste der Haushalte
     * @throws HouseholdServiceException e
     */
    List<HouseholdAggregate> getAllByVppId(String virtualPowerPlantId) throws HouseholdServiceException;

    /**
     * Holt ein spezifisches Haushalt
     *
     * @param householdId Id des Haushalts
     * @return Haushalt
     * @throws HouseholdServiceException e
     */
    HouseholdAggregate get(String householdId) throws HouseholdServiceException;

    /**
     * Persistiert ein Haushalt und weist es dem VK zu
     *
     * @param domainEntity        Haushalt
     * @param virtualPowerPlantId Id des VK
     * @throws HouseholdServiceException e
     */
    void save(HouseholdAggregate domainEntity, String virtualPowerPlantId) throws HouseholdServiceException;

    /**
     * Löscht ein Haushalt aus einem VK
     *
     * @param householdId         Id des Haushalts
     * @param virtualPowerPlantId Id des VK
     * @throws HouseholdServiceException e
     */
    void delete(String householdId, String virtualPowerPlantId) throws HouseholdServiceException;

    /**
     * Aktualisiert ein Haushalt eines VK
     *
     * @param householdId         Id des Haushalts
     * @param domainEntity        aktualisierte Daten des Haushalts
     * @param virtualPowerPlantId Id des VK
     * @throws HouseholdServiceException e
     */
    void update(String householdId, HouseholdAggregate domainEntity, String virtualPowerPlantId) throws HouseholdServiceException;
}
