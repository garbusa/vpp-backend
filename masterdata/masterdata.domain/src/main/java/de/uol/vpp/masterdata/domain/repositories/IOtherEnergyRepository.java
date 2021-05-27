package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das Repository von alternativen Erzeugungsanlagen
 */
public interface IOtherEnergyRepository {
    /**
     * Holt alle alternativen Erzeugungsanlagen eines DK
     *
     * @param decentralizedPowerPlantAggregate DK
     * @return Liste alternativer Erzeugungsanlagen
     * @throws ProducerRepositoryException e
     */
    List<OtherEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Holt alle alternativen Erzeugungsanlagen eines Haushalts
     *
     * @param householdAggregate Haushalt
     * @return Liste alternativer Erzeugungsanlagen
     * @throws ProducerRepositoryException e
     */
    List<OtherEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Holt eine alternative Erzeugungsanlage
     *
     * @param id Id der alternativen Erzeugungsanlage
     * @return alternative Erzeugungsanlage
     * @throws ProducerRepositoryException e
     */
    Optional<OtherEnergyEntity> getById(OtherEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Persistiert eine alternative Erzeugungsanlage
     *
     * @param domainEntity alternative Erzeugungsanlage
     * @throws ProducerRepositoryException e
     */
    void save(OtherEnergyEntity domainEntity) throws ProducerRepositoryException;

    /**
     * Weist alternative Erzeugungsanlage einem DK zu
     *
     * @param domainEntity                     alternative Erzeugungsanlage
     * @param decentralizedPowerPlantAggregate DK
     * @throws ProducerRepositoryException e
     */
    void assignToDecentralizedPowerPlant(OtherEnergyEntity domainEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Weist alternative Erzeugungsanlage einem Haushalt zu
     *
     * @param domainEntity       alternative Erzeugungsanlage
     * @param householdAggregate Haushalt
     * @throws ProducerRepositoryException e
     */
    void assignToHousehold(OtherEnergyEntity domainEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Löscht eine alternative Erzeugunsanlage
     *
     * @param id Id der alternativen Erzeugungsanlage
     * @throws ProducerRepositoryException e
     */
    void deleteById(OtherEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Aktualisiert eine alternative Erzeugungsanlage
     *
     * @param id           Id der alternativen Erzeugungsanlage
     * @param domainEntity alternative Erzeugungsanlage
     * @throws ProducerRepositoryException e
     */
    void update(OtherEnergyIdVO id, OtherEnergyEntity domainEntity) throws ProducerRepositoryException;
}
