package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das Haushalt-Repository
 */
public interface IHouseholdRepository {
    /**
     * Holt alle Haushalts eines VK
     *
     * @param virtualPowerPlantAggregate VK
     * @return Liste von Haushalten
     * @throws HouseholdRepositoryException e
     */
    List<HouseholdAggregate> getAllByVirtualPowerPlant(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws HouseholdRepositoryException;

    /**
     * Holt ein spezifisches Haushalt
     *
     * @param id Id des Haushalts
     * @return Haushalt
     * @throws HouseholdRepositoryException e
     */
    Optional<HouseholdAggregate> getById(HouseholdIdVO id) throws HouseholdRepositoryException;

    /**
     * Persistiert ein Haushalt
     *
     * @param entity Haushalt
     * @throws HouseholdRepositoryException e
     */
    void save(HouseholdAggregate entity) throws HouseholdRepositoryException;

    /**
     * Löscht ein Haushalt
     *
     * @param id Id des Haushalts
     * @throws HouseholdRepositoryException e
     */
    void deleteById(HouseholdIdVO id) throws HouseholdRepositoryException;

    /**
     * Weist Haushalt einem VK zu
     *
     * @param entity            Haushalt
     * @param virtualPowerPlant VK
     * @throws HouseholdRepositoryException e
     */
    void assign(HouseholdAggregate entity, VirtualPowerPlantAggregate virtualPowerPlant) throws HouseholdRepositoryException;

    /**
     * Aktualisiert ein existierenden Haushalt
     *
     * @param id           Id des Haushalts
     * @param domainEntity aktualisierte Daten
     * @throws HouseholdRepositoryException e
     */
    void update(HouseholdIdVO id, HouseholdAggregate domainEntity) throws HouseholdRepositoryException;
}
