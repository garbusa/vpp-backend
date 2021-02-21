package de.uol.vpp.masterdata.domain.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerIdVO;

import java.util.List;
import java.util.Optional;

public interface IConsumerRepository {
    List<ConsumerEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ConsumerrRepositoryException;

    Optional<ConsumerEntity> getById(ConsumerIdVO id) throws ConsumerrRepositoryException;

    void save(ConsumerEntity consumerEntity) throws ConsumerrRepositoryException;

    void assign(ConsumerEntity entity, HouseholdAggregate householdAggregate) throws ConsumerrRepositoryException;

    void deleteById(ConsumerIdVO id) throws ConsumerrRepositoryException;
}
