package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.exceptions.ConsumerException;
import de.uol.vpp.masterdata.domain.repositories.ConsumerRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IConsumerRepository;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerStatusVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.Consumer;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ConsumerStatus;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.ConsumerJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConsumerRepositoryImpl implements IConsumerRepository {

    private final ConsumerJpaRepository jpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<ConsumerEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ConsumerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneByBusinessKey(householdAggregate.getHouseholdId().getId());

            if (household.isPresent()) {
                List<ConsumerEntity> list = new ArrayList<>();
                for (Consumer consumer : jpaRepository.findAllByHousehold(household.get())) {
                    ConsumerEntity toDomain = converter.toDomain(consumer);
                    list.add(toDomain);
                }
                return list;
            } else {
                throw new ConsumerRepositoryException(String.format("Can not find household %s to get all consumers", householdAggregate.getHouseholdId().getId()));
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConsumerRepositoryException("failed to get all consumer. constraint violation occured.");
        } catch (ConsumerException e) {
            throw new ConsumerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<ConsumerEntity> getById(ConsumerIdVO id) throws ConsumerRepositoryException {
        try {
            Optional<Consumer> result = jpaRepository.findOneByBusinessKey(id.getId());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            } else {
                return Optional.empty();
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConsumerRepositoryException("failed to get consumer. constraint violation occured.");
        } catch (ConsumerException e) {
            throw new ConsumerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(ConsumerEntity consumerEntity) throws ConsumerRepositoryException {
        try {
            Consumer jpaEntity = converter.toInfrastructure(consumerEntity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (DataIntegrityViolationException e) {
            throw new ConsumerRepositoryException("failed to save consumer. constraint violation occured.");
        }
    }

    @Override
    public void assign(ConsumerEntity consumerEntity, HouseholdAggregate householdAggregate) throws ConsumerRepositoryException {
        try {
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
                        throw new ConsumerRepositoryException(
                                String.format("To assign an entity for consumer %s, the assigments have to be empty", consumerEntity.getConsumerId().getId())
                        );
                    }
                } else {
                    throw new ConsumerRepositoryException(
                            String.format("Failed to fetch consumer %s", consumerEntity.getConsumerId().getId())
                    );
                }
            } else {
                throw new ConsumerRepositoryException(
                        String.format("Household %s does not exist. Failed to fetch all consumer", householdAggregate.getHouseholdId().getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConsumerRepositoryException("failed to assign consumer. constraint violation occured.");
        }
    }

    @Override
    public void deleteById(ConsumerIdVO id) throws ConsumerRepositoryException {
        try {
            Optional<Consumer> jpaEntity = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntity.isPresent()) {
                try {
                    jpaRepository.delete(jpaEntity.get());
                } catch (Exception e) {
                    throw new ConsumerRepositoryException(e.getMessage(), e);
                }
            } else {
                throw new ConsumerRepositoryException(
                        String.format("consumer %s can not be found and can not be deleted", id.getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConsumerRepositoryException("failed to delete consumer. constraint violation occured.");
        }
    }

    @Override
    public void updateStatus(ConsumerIdVO id, ConsumerStatusVO status) throws ConsumerRepositoryException {
        try {
            Optional<Consumer> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntityOptional.isPresent()) {
                try {
                    Consumer jpaEntity = jpaEntityOptional.get();
                    ConsumerStatus newStatus = new ConsumerStatus();
                    newStatus.setRunning(status.isRunning());
                    jpaEntity.setConsumerStatus(newStatus);
                    jpaRepository.save(jpaEntity);
                } catch (Exception e) {
                    throw new ConsumerRepositoryException(e.getMessage(), e);
                }
            } else {
                throw new ConsumerRepositoryException(
                        String.format("consumer %s can not be found therefore status can not be toggled", id.getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConsumerRepositoryException("failed to update consumer status. constraint violation occured.");
        }
    }

    @Override
    public void update(ConsumerIdVO id, ConsumerEntity domainEntity) throws ConsumerRepositoryException {
        try {
            Optional<Consumer> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntityOptional.isPresent()) {
                Consumer jpaEntity = jpaEntityOptional.get();
                Consumer updated = converter.toInfrastructure(domainEntity);
                jpaEntity.setBusinessKey(updated.getBusinessKey());
                jpaEntity.setConsumerPower(updated.getConsumerPower());
                jpaEntity.setConsumerStatus(updated.getConsumerStatus());
                jpaRepository.saveAndFlush(jpaEntity);
            } else {
                throw new ConsumerRepositoryException("failed to update consumer. can not find consumer entity.");
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConsumerRepositoryException("failed to update consumer. constraint violation occured.");
        }
    }

}
