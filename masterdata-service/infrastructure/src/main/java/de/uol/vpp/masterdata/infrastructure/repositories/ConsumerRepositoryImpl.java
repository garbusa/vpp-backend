package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.repositories.ConsumerrRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IConsumerRepository;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.Consumer;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.ConsumerJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConsumerRepositoryImpl implements IConsumerRepository {

    private final ConsumerJpaRepository jpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<ConsumerEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ConsumerrRepositoryException {
        Optional<Household> household = householdJpaRepository
                .findOneByBusinessKey(householdAggregate.getHouseholdId().getId());

        if (household.isPresent()) {
            return jpaRepository.findAllByHousehold(household.get())
                    .stream().map(converter::toDomain).collect(Collectors.toList());
        } else {
            throw new ConsumerrRepositoryException(String.format("Can not find household %s to get all consumers", householdAggregate.getHouseholdId().getId()));
        }
    }

    @Override
    public Optional<ConsumerEntity> getById(ConsumerIdVO id) throws ConsumerrRepositoryException {
        Optional<Consumer> result = jpaRepository.findOneByBusinessKey(id.getId());
        return result.map(converter::toDomain);
    }

    @Override
    public void save(ConsumerEntity consumerEntity) throws ConsumerrRepositoryException {
        try {
            Consumer jpaEntity = converter.toInfrastructure(consumerEntity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (Exception e) {
            throw new ConsumerrRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void assign(ConsumerEntity consumerEntity, HouseholdAggregate householdAggregate) throws ConsumerrRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneByBusinessKey(householdAggregate.getHouseholdId().getId());
        if (household.isPresent()) {
            Optional<Consumer> consumer = jpaRepository.findOneByBusinessKey(consumerEntity.getConsumerId().getId());
            if (consumer.isPresent()) {
                if (consumer.get().getHousehold() == null) {
                    consumer.get().setHousehold(household.get());
                    jpaRepository.saveAndFlush(consumer.get());
                    household.get().getConsumers().add(consumer.get());
                    householdJpaRepository.saveAndFlush(household.get());
                } else {
                    throw new ConsumerrRepositoryException(
                            String.format("To assign an entity for consumer %s, the assigments have to be empty", consumerEntity.getConsumerId().getId())
                    );
                }
            } else {
                throw new ConsumerrRepositoryException(
                        String.format("Failed to fetch consumer %s", consumerEntity.getConsumerId().getId())
                );
            }
        } else {
            throw new ConsumerrRepositoryException(
                    String.format("Household %s does not exist. Failed to fetch all consumer", householdAggregate.getHouseholdId().getId())
            );
        }
    }

    @Override
    public void deleteById(ConsumerIdVO id) throws ConsumerrRepositoryException {
        Optional<Consumer> jpaEntity = jpaRepository.findOneByBusinessKey(id.getId());
        if (jpaEntity.isPresent()) {
            try {
                jpaRepository.delete(jpaEntity.get());
            } catch (Exception e) {
                throw new ConsumerrRepositoryException(e.getMessage(), e);
            }
        } else {
            throw new ConsumerrRepositoryException(
                    String.format("consumer %s can not be found and can not be deleted", id.getId())
            );
        }
    }
}
