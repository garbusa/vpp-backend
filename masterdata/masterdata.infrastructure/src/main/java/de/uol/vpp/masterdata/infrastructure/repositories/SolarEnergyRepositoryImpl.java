package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.repositories.ISolarEnergyRepository;
import de.uol.vpp.masterdata.domain.repositories.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.SolarEnergyIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.SolarEnergy;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.SolarEnergyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SolarEnergyRepositoryImpl implements ISolarEnergyRepository {

    private final SolarEnergyJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<SolarEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                    .findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
            if (dpp.isPresent()) {
                List<SolarEnergyEntity> result = new ArrayList<>();
                for (SolarEnergy SolarEnergy : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(SolarEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("DK %s konnte nicht gefunden werden um Solaranlagen abzufragen", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<SolarEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneById(householdAggregate.getHouseholdId().getValue());
            if (household.isPresent()) {
                List<SolarEnergyEntity> result = new ArrayList<>();
                for (SolarEnergy SolarEnergy : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(SolarEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Haushalt %s konnte nicht gefunden werden um Solaranlagen abzufragen", householdAggregate.getHouseholdId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<SolarEnergyEntity> getById(SolarEnergyIdVO id) throws ProducerRepositoryException {
        try {
            Optional<SolarEnergy> result = jpaRepository.findOneById(id.getValue());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(SolarEnergyEntity SolarEnergyEntity) throws ProducerRepositoryException {
        SolarEnergy jpaEntity = converter.toInfrastructure(SolarEnergyEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assignToDecentralizedPowerPlant(SolarEnergyEntity SolarEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
        if (dpp.isPresent()) {
            Optional<SolarEnergy> Solar = jpaRepository.findOneById(SolarEnergyEntity.getId().getValue());
            if (Solar.isPresent()) {
                if (Solar.get().getDecentralizedPowerPlant() == null &&
                        Solar.get().getHousehold() == null) {
                    Solar.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(Solar.get());
                    dpp.get().getSolars().add(Solar.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("Solaranlage %s konnte DK nicht zugewiesen werden, da Solaranlage bereits zugewiesen wurde", SolarEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Solaranlage %s konnte nicht gefunden werden", SolarEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Solaranlage %s konnte nicht zugewiesen werden, da DK %s nicht gefunden wurde", SolarEnergyEntity.getId().getValue(), decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void assignToHousehold(SolarEnergyEntity SolarEnergyEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneById(householdAggregate.getHouseholdId().getValue());
        if (household.isPresent()) {
            Optional<SolarEnergy> SolarEnergy = jpaRepository.findOneById(SolarEnergyEntity.getId().getValue());
            if (SolarEnergy.isPresent()) {
                if (SolarEnergy.get().getDecentralizedPowerPlant() == null &&
                        SolarEnergy.get().getHousehold() == null) {
                    SolarEnergy.get().setHousehold(household.get());
                    jpaRepository.save(SolarEnergy.get());
                    household.get().getSolars().add(SolarEnergy.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("Solaranlage %s konnte Haushalt nicht zugewiesen werden, da Solaranlage bereits zugewiesen wurde", SolarEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Solaranlage %s konnte nicht gefunden werden", SolarEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Solaranlage %s konnte nicht zugewiesen werden, da Haushalt %s nicht gefunden wurde", SolarEnergyEntity.getId().getValue(), householdAggregate.getHouseholdId().getValue())
            );
        }
    }

    @Override
    public void deleteById(SolarEnergyIdVO id) throws ProducerRepositoryException {
        Optional<SolarEnergy> jpaEntity = jpaRepository.findOneById(id.getValue());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new ProducerRepositoryException(
                    String.format("Solaranlage %s konnte nicht gelöscht werden, da Solaranlage nicht gefunden wurde", id.getValue())
            );
        }
    }

    @Override
    public void update(SolarEnergyIdVO id, SolarEnergyEntity domainEntity) throws ProducerRepositoryException {
        Optional<SolarEnergy> jpaEntityOptional = jpaRepository.findOneById(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            SolarEnergy jpaEntity = jpaEntityOptional.get();
            SolarEnergy updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setId(updated.getId());
            jpaEntity.setCapacity(updated.getCapacity());
            jpaEntity.setRatedCapacity(updated.getRatedCapacity());
            jpaEntity.setAlignment(updated.getAlignment());
            jpaEntity.setSlope(updated.getSlope());
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException(
                    String.format("Solaranlage %s konnte nicht aktualisiert werden, da Solaranlage nicht gefunden wurde", id.getValue())
            );
        }
    }

}
