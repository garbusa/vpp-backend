package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.WindEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.WindEnergyIdVO;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstellendefinition für das Repository von Windkraftanlagen
 */
public interface IWindEnergyRepository {
    /**
     * Holt alle Windkraftanlagen eines DK
     *
     * @param decentralizedPowerPlantAggregate DK
     * @return Liste von Windkraftanlagen
     * @throws ProducerRepositoryException e
     */
    List<WindEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Holt alle Windkraftanlagen eines Haushalts
     *
     * @param householdAggregate Haushalt
     * @return Liste von Windkraftanlagen
     * @throws ProducerRepositoryException e
     */
    List<WindEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Holt eine Windkraftanlage
     *
     * @param id Id der Windkraftanlage
     * @return Windkraftanlage
     * @throws ProducerRepositoryException e
     */
    Optional<WindEnergyEntity> getById(WindEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Persistiert eine Windkraftanlage
     *
     * @param domainEntity Windkraftanlage
     * @throws ProducerRepositoryException e
     */
    void save(WindEnergyEntity domainEntity) throws ProducerRepositoryException;

    /**
     * Weist Windkraftanlage einem DK zu
     *
     * @param domainEntity                     Windkraftanlage
     * @param decentralizedPowerPlantAggregate DK
     * @throws ProducerRepositoryException e
     */
    void assignToDecentralizedPowerPlant(WindEnergyEntity domainEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    /**
     * Weist Windkraftanlage einem Haushalt zu
     *
     * @param domainEntity       Windkraftanlage
     * @param householdAggregate Haushalt
     * @throws ProducerRepositoryException e
     */
    void assignToHousehold(WindEnergyEntity domainEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    /**
     * Löscht eine alternative Erzeugunsanlage
     *
     * @param id Id der Windkraftanlage
     * @throws ProducerRepositoryException e
     */
    void deleteById(WindEnergyIdVO id) throws ProducerRepositoryException;

    /**
     * Aktualisiert eine Windkraftanlage
     *
     * @param id           Id der Windkraftanlage
     * @param domainEntity Windkraftanlage
     * @throws ProducerRepositoryException e
     */
    void update(WindEnergyIdVO id, WindEnergyEntity domainEntity) throws ProducerRepositoryException;
}
