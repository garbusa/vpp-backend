package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerStatusVO;

import java.util.List;
import java.util.Optional;

public interface IConsumerRepository {
    List<ConsumerEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ConsumerRepositoryException;

    Optional<ConsumerEntity> getById(ConsumerIdVO id) throws ConsumerRepositoryException;

    void save(ConsumerEntity consumerEntity) throws ConsumerRepositoryException;

    void assign(ConsumerEntity entity, HouseholdAggregate householdAggregate) throws ConsumerRepositoryException;

    void deleteById(ConsumerIdVO id) throws ConsumerRepositoryException;

    void updateStatus(ConsumerIdVO id, ConsumerStatusVO status) throws ConsumerRepositoryException;

    void update(ConsumerIdVO id, ConsumerEntity domainEntity) throws ConsumerRepositoryException;
}
