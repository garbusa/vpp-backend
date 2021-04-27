package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.WindEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.repositories.IWindEnergyRepository;
import de.uol.vpp.masterdata.domain.repositories.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.WindEnergyIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.WindEnergy;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.WindEnergyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WindEnergyRepositoryImpl implements IWindEnergyRepository {

    private final WindEnergyJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<WindEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                    .findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
            if (dpp.isPresent()) {
                List<WindEnergyEntity> result = new ArrayList<>();
                for (WindEnergy WindEnergy : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(WindEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find dpp %s to get all WindEnergys", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<WindEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneByBusinessKey(householdAggregate.getHouseholdId().getValue());
            if (household.isPresent()) {
                List<WindEnergyEntity> result = new ArrayList<>();
                for (WindEnergy WindEnergy : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(WindEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find household %s to get all WindEnergys", householdAggregate.getHouseholdId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<WindEnergyEntity> getById(WindEnergyIdVO id) throws ProducerRepositoryException {
        try {
            Optional<WindEnergy> result = jpaRepository.findOneByBusinessKey(id.getValue());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(WindEnergyEntity WindEnergyEntity) throws ProducerRepositoryException {
        WindEnergy jpaEntity = converter.toInfrastructure(WindEnergyEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assignToDecentralizedPowerPlant(WindEnergyEntity windEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
        if (dpp.isPresent()) {
            Optional<WindEnergy> wind = jpaRepository.findOneByBusinessKey(windEnergyEntity.getId().getValue());
            if (wind.isPresent()) {
                if (wind.get().getDecentralizedPowerPlant() == null &&
                        wind.get().getHousehold() == null) {
                    wind.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(wind.get());
                    dpp.get().getWinds().add(wind.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for WindEnergy %s, the assigments have to be empty", windEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch WindEnergy %s", windEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Dpp %s does not exist. Failed to fetch all WindEnergy", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void assignToHousehold(WindEnergyEntity WindEnergyEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneByBusinessKey(householdAggregate.getHouseholdId().getValue());
        if (household.isPresent()) {
            Optional<WindEnergy> WindEnergy = jpaRepository.findOneByBusinessKey(WindEnergyEntity.getId().getValue());
            if (WindEnergy.isPresent()) {
                if (WindEnergy.get().getDecentralizedPowerPlant() == null &&
                        WindEnergy.get().getHousehold() == null) {
                    WindEnergy.get().setHousehold(household.get());
                    jpaRepository.save(WindEnergy.get());
                    household.get().getWinds().add(WindEnergy.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for WindEnergy %s, the assigments have to be empty", WindEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch WindEnergy %s", WindEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Household %s does not exist. Failed to fetch all WindEnergy", householdAggregate.getHouseholdId().getValue())
            );
        }
    }

    @Override
    public void deleteById(WindEnergyIdVO id) throws ProducerRepositoryException {
        Optional<WindEnergy> jpaEntity = jpaRepository.findOneByBusinessKey(id.getValue());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new ProducerRepositoryException(
                    String.format("WindEnergy %s can not be found and can not be deleted", id.getValue())
            );
        }
    }

    @Override
    public void update(WindEnergyIdVO id, WindEnergyEntity domainEntity) throws ProducerRepositoryException {
        Optional<WindEnergy> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            WindEnergy jpaEntity = jpaEntityOptional.get();
            WindEnergy updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setBusinessKey(updated.getBusinessKey());
            jpaEntity.setLongitude(updated.getLongitude());
            jpaEntity.setLatitude(updated.getLatitude());
            jpaEntity.setCapacity(updated.getCapacity());
            jpaEntity.setEfficiency(updated.getEfficiency());
            jpaEntity.setHeight(updated.getHeight());
            jpaEntity.setRadius(updated.getRadius());
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException("failed to update WindEnergy. can not find WindEnergy entity.");
        }
    }

}
