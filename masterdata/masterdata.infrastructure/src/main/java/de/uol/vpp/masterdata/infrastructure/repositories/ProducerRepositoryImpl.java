package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.repositories.IProducerRepository;
import de.uol.vpp.masterdata.domain.repositories.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerStatusVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.Producer;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ProducerStatus;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.ProducerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProducerRepositoryImpl implements IProducerRepository {

    private final ProducerJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<ProducerEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                    .findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId());
            if (dpp.isPresent()) {
                List<ProducerEntity> result = new ArrayList<>();
                for (Producer producer : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(producer));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find dpp %s to get all producers", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<ProducerEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneByBusinessKey(householdAggregate.getHouseholdId().getId());
            if (household.isPresent()) {
                List<ProducerEntity> result = new ArrayList<>();
                for (Producer producer : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(producer));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find household %s to get all producers", householdAggregate.getHouseholdId().getId()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<ProducerEntity> getById(ProducerIdVO id) throws ProducerRepositoryException {
        try {
            Optional<Producer> result = jpaRepository.findOneByBusinessKey(id.getId());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(ProducerEntity producerEntity) throws ProducerRepositoryException {
        Producer jpaEntity = converter.toInfrastructure(producerEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assignToDecentralizedPowerPlant(ProducerEntity producerEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId());
        if (dpp.isPresent()) {
            Optional<Producer> producer = jpaRepository.findOneByBusinessKey(producerEntity.getProducerId().getId());
            if (producer.isPresent()) {
                if (producer.get().getDecentralizedPowerPlant() == null &&
                        producer.get().getHousehold() == null) {
                    producer.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(producer.get());
                    dpp.get().getProducers().add(producer.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for producer %s, the assigments have to be empty", producerEntity.getProducerId().getId())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch producer %s", producerEntity.getProducerId().getId())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Dpp %s does not exist. Failed to fetch all Producer", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId())
            );
        }
    }

    @Override
    public void assignToHousehold(ProducerEntity producerEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneByBusinessKey(householdAggregate.getHouseholdId().getId());
        if (household.isPresent()) {
            Optional<Producer> producer = jpaRepository.findOneByBusinessKey(producerEntity.getProducerId().getId());
            if (producer.isPresent()) {
                if (producer.get().getDecentralizedPowerPlant() == null &&
                        producer.get().getHousehold() == null) {
                    producer.get().setHousehold(household.get());
                    jpaRepository.save(producer.get());
                    household.get().getProducers().add(producer.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for producer %s, the assigments have to be empty", producerEntity.getProducerId().getId())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch producer %s", producerEntity.getProducerId().getId())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Household %s does not exist. Failed to fetch all Producer", householdAggregate.getHouseholdId().getId())
            );
        }
    }

    @Override
    public void deleteById(ProducerIdVO id) throws ProducerRepositoryException {
        Optional<Producer> jpaEntity = jpaRepository.findOneByBusinessKey(id.getId());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new ProducerRepositoryException(
                    String.format("producer %s can not be found and can not be deleted", id.getId())
            );
        }
    }

    @Override
    public void updateStatus(ProducerIdVO id, ProducerStatusVO status) throws ProducerRepositoryException {
        Optional<Producer> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
        if (jpaEntityOptional.isPresent()) {
            Producer jpaEntity = jpaEntityOptional.get();
            ProducerStatus newStatus = new ProducerStatus();
            newStatus.setCapacity(status.getCapacity());
            newStatus.setRunning(status.isRunning());
            jpaEntity.setProducerStatus(newStatus);
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException(
                    String.format("producer %s can not be found therefore status can not be updated", id.getId())
            );
        }
    }

    @Override
    public void update(ProducerIdVO id, ProducerEntity domainEntity) throws ProducerRepositoryException {
        Optional<Producer> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
        if (jpaEntityOptional.isPresent()) {
            Producer jpaEntity = jpaEntityOptional.get();
            Producer updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setBusinessKey(updated.getBusinessKey());
            jpaEntity.setProducerStatus(updated.getProducerStatus());
            jpaEntity.setProducerPower(updated.getProducerPower());
            jpaEntity.setProducerType(updated.getProducerType());
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException("failed to update producer. can not find producer entity.");
        }
    }

}
