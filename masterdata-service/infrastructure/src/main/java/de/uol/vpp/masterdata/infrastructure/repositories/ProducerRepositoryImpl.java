package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.repositories.IProducerRepository;
import de.uol.vpp.masterdata.domain.repositories.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.Producer;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.ProducerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProducerRepositoryImpl implements IProducerRepository {

    private final ProducerJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<ProducerEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                .findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId());

        if (dpp.isPresent()) {
            return jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())
                    .stream().map(converter::toDomain).collect(Collectors.toList());
        } else {
            throw new ProducerRepositoryException(String.format("Can not find dpp %s to get all producers", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getId()));
        }

    }

    @Override
    public List<ProducerEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository
                .findOneByBusinessKey(householdAggregate.getHouseholdId().getId());

        if (household.isPresent()) {
            return jpaRepository.findAllByHousehold(household.get())
                    .stream().map(converter::toDomain).collect(Collectors.toList());
        } else {
            throw new ProducerRepositoryException(String.format("Can not find household %s to get all producers", householdAggregate.getHouseholdId().getId()));
        }
    }

    @Override
    public Optional<ProducerEntity> getById(ProducerIdVO id) throws ProducerRepositoryException {
        Optional<Producer> result = jpaRepository.findOneByBusinessKey(id.getId());
        return result.map(converter::toDomain);
    }

    @Override
    public void save(ProducerEntity producerEntity) throws ProducerRepositoryException {
        try {
            Producer jpaEntity = converter.toInfrastructure(producerEntity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (Exception e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
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
                    jpaRepository.saveAndFlush(producer.get());
                    dpp.get().getProducers().add(producer.get());
                    decentralizedPowerPlantJpaRepository.saveAndFlush(dpp.get());
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
                    jpaRepository.saveAndFlush(producer.get());
                    household.get().getProducers().add(producer.get());
                    householdJpaRepository.saveAndFlush(household.get());
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
            try {
                jpaRepository.delete(jpaEntity.get());
            } catch (Exception e) {
                throw new ProducerRepositoryException(e.getMessage(), e);
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("producer %s can not be found and can not be deleted", id.getId())
            );
        }
    }
}
