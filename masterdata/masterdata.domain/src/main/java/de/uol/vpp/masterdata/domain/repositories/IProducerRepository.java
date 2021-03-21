package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerStatusVO;

import java.util.List;
import java.util.Optional;

public interface IProducerRepository {
    List<ProducerEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    List<ProducerEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    Optional<ProducerEntity> getById(ProducerIdVO id) throws ProducerRepositoryException;

    void save(ProducerEntity producerEntity) throws ProducerRepositoryException;

    void assignToDecentralizedPowerPlant(ProducerEntity producerEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException;

    void assignToHousehold(ProducerEntity entity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException;

    void deleteById(ProducerIdVO id) throws ProducerRepositoryException;

    void updateStatus(ProducerIdVO producerIdVO, ProducerStatusVO producerStatusVO) throws ProducerRepositoryException;

    void update(ProducerIdVO id, ProducerEntity domainEntity) throws ProducerRepositoryException;
}
