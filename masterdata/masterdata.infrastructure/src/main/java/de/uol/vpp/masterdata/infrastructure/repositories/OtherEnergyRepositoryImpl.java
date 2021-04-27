package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.repositories.IOtherEnergyRepository;
import de.uol.vpp.masterdata.domain.repositories.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.OtherEnergy;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.OtherEnergyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OtherEnergyRepositoryImpl implements IOtherEnergyRepository {

    private final OtherEnergyJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<OtherEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                    .findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
            if (dpp.isPresent()) {
                List<OtherEnergyEntity> result = new ArrayList<>();
                for (OtherEnergy otherEnergy : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(otherEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find dpp %s to get all OtherEnergys", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<OtherEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneByBusinessKey(householdAggregate.getHouseholdId().getValue());
            if (household.isPresent()) {
                List<OtherEnergyEntity> result = new ArrayList<>();
                for (OtherEnergy OtherEnergy : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(OtherEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find household %s to get all OtherEnergys", householdAggregate.getHouseholdId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<OtherEnergyEntity> getById(OtherEnergyIdVO id) throws ProducerRepositoryException {
        try {
            Optional<OtherEnergy> result = jpaRepository.findOneByBusinessKey(id.getValue());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(OtherEnergyEntity OtherEnergyEntity) throws ProducerRepositoryException {
        OtherEnergy jpaEntity = converter.toInfrastructure(OtherEnergyEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assignToDecentralizedPowerPlant(OtherEnergyEntity otherEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
        if (dpp.isPresent()) {
            Optional<OtherEnergy> other = jpaRepository.findOneByBusinessKey(otherEnergyEntity.getId().getValue());
            if (other.isPresent()) {
                if (other.get().getDecentralizedPowerPlant() == null &&
                        other.get().getHousehold() == null) {
                    other.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(other.get());
                    dpp.get().getOthers().add(other.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for OtherEnergy %s, the assigments have to be empty", otherEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch OtherEnergy %s", otherEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Dpp %s does not exist. Failed to fetch all OtherEnergy", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void assignToHousehold(OtherEnergyEntity OtherEnergyEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneByBusinessKey(householdAggregate.getHouseholdId().getValue());
        if (household.isPresent()) {
            Optional<OtherEnergy> OtherEnergy = jpaRepository.findOneByBusinessKey(OtherEnergyEntity.getId().getValue());
            if (OtherEnergy.isPresent()) {
                if (OtherEnergy.get().getDecentralizedPowerPlant() == null &&
                        OtherEnergy.get().getHousehold() == null) {
                    OtherEnergy.get().setHousehold(household.get());
                    jpaRepository.save(OtherEnergy.get());
                    household.get().getOthers().add(OtherEnergy.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for OtherEnergy %s, the assigments have to be empty", OtherEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch OtherEnergy %s", OtherEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Household %s does not exist. Failed to fetch all OtherEnergy", householdAggregate.getHouseholdId().getValue())
            );
        }
    }

    @Override
    public void deleteById(OtherEnergyIdVO id) throws ProducerRepositoryException {
        Optional<OtherEnergy> jpaEntity = jpaRepository.findOneByBusinessKey(id.getValue());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new ProducerRepositoryException(
                    String.format("OtherEnergy %s can not be found and can not be deleted", id.getValue())
            );
        }
    }

    @Override
    public void update(OtherEnergyIdVO id, OtherEnergyEntity domainEntity) throws ProducerRepositoryException {
        Optional<OtherEnergy> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            OtherEnergy jpaEntity = jpaEntityOptional.get();
            OtherEnergy updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setBusinessKey(updated.getBusinessKey());
            jpaEntity.setRatedCapacity(updated.getRatedCapacity());
            jpaEntity.setCapacity(updated.getCapacity());
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException("failed to update OtherEnergy. can not find OtherEnergy entity.");
        }
    }

}
