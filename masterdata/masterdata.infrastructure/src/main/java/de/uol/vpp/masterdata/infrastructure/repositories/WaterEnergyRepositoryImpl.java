package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.repositories.IWaterEnergyRepository;
import de.uol.vpp.masterdata.domain.repositories.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.WaterEnergyIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.WaterEnergy;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.WaterEnergyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WaterEnergyRepositoryImpl implements IWaterEnergyRepository {

    private final WaterEnergyJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<WaterEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                    .findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
            if (dpp.isPresent()) {
                List<WaterEnergyEntity> result = new ArrayList<>();
                for (WaterEnergy WaterEnergy : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(WaterEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find dpp %s to get all WaterEnergys", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<WaterEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneByBusinessKey(householdAggregate.getHouseholdId().getValue());
            if (household.isPresent()) {
                List<WaterEnergyEntity> result = new ArrayList<>();
                for (WaterEnergy WaterEnergy : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(WaterEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Can not find household %s to get all WaterEnergys", householdAggregate.getHouseholdId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<WaterEnergyEntity> getById(WaterEnergyIdVO id) throws ProducerRepositoryException {
        try {
            Optional<WaterEnergy> result = jpaRepository.findOneByBusinessKey(id.getValue());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(WaterEnergyEntity WaterEnergyEntity) throws ProducerRepositoryException {
        WaterEnergy jpaEntity = converter.toInfrastructure(WaterEnergyEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assignToDecentralizedPowerPlant(WaterEnergyEntity WaterEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneByBusinessKey(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
        if (dpp.isPresent()) {
            Optional<WaterEnergy> Water = jpaRepository.findOneByBusinessKey(WaterEnergyEntity.getId().getValue());
            if (Water.isPresent()) {
                if (Water.get().getDecentralizedPowerPlant() == null &&
                        Water.get().getHousehold() == null) {
                    Water.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(Water.get());
                    dpp.get().getWaters().add(Water.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for WaterEnergy %s, the assigments have to be empty", WaterEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch WaterEnergy %s", WaterEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Dpp %s does not exist. Failed to fetch all WaterEnergy", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void assignToHousehold(WaterEnergyEntity WaterEnergyEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneByBusinessKey(householdAggregate.getHouseholdId().getValue());
        if (household.isPresent()) {
            Optional<WaterEnergy> WaterEnergy = jpaRepository.findOneByBusinessKey(WaterEnergyEntity.getId().getValue());
            if (WaterEnergy.isPresent()) {
                if (WaterEnergy.get().getDecentralizedPowerPlant() == null &&
                        WaterEnergy.get().getHousehold() == null) {
                    WaterEnergy.get().setHousehold(household.get());
                    jpaRepository.save(WaterEnergy.get());
                    household.get().getWaters().add(WaterEnergy.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("To assign an entity for WaterEnergy %s, the assigments have to be empty", WaterEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Failed to fetch WaterEnergy %s", WaterEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Household %s does not exist. Failed to fetch all WaterEnergy", householdAggregate.getHouseholdId().getValue())
            );
        }
    }

    @Override
    public void deleteById(WaterEnergyIdVO id) throws ProducerRepositoryException {
        Optional<WaterEnergy> jpaEntity = jpaRepository.findOneByBusinessKey(id.getValue());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new ProducerRepositoryException(
                    String.format("WaterEnergy %s can not be found and can not be deleted", id.getValue())
            );
        }
    }

    @Override
    public void update(WaterEnergyIdVO id, WaterEnergyEntity domainEntity) throws ProducerRepositoryException {
        Optional<WaterEnergy> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            WaterEnergy jpaEntity = jpaEntityOptional.get();
            WaterEnergy updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setBusinessKey(updated.getBusinessKey());
            jpaEntity.setCapacity(updated.getCapacity());
            jpaEntity.setEfficiency(updated.getEfficiency());
            jpaEntity.setHeight(updated.getHeight());
            jpaEntity.setDensity(updated.getDensity());
            jpaEntity.setGravity(updated.getGravity());
            jpaEntity.setVolumeFlow(updated.getVolumeFlow());
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException("failed to update WaterEnergy. can not find WaterEnergy entity.");
        }
    }

}
