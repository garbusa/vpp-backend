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
                    .findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
            if (dpp.isPresent()) {
                List<WindEnergyEntity> result = new ArrayList<>();
                for (WindEnergy WindEnergy : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(WindEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("DK %s konnte nicht gefunden werden um Windkraftanlage abzufragen", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<WindEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneById(householdAggregate.getHouseholdId().getValue());
            if (household.isPresent()) {
                List<WindEnergyEntity> result = new ArrayList<>();
                for (WindEnergy WindEnergy : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(WindEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Haushalt %s konnte nicht gefunden werden um Windkraftanlage abzufragen", householdAggregate.getHouseholdId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<WindEnergyEntity> getById(WindEnergyIdVO id) throws ProducerRepositoryException {
        try {
            Optional<WindEnergy> result = jpaRepository.findOneById(id.getValue());
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
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
        if (dpp.isPresent()) {
            Optional<WindEnergy> wind = jpaRepository.findOneById(windEnergyEntity.getId().getValue());
            if (wind.isPresent()) {
                if (wind.get().getDecentralizedPowerPlant() == null &&
                        wind.get().getHousehold() == null) {
                    wind.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(wind.get());
                    dpp.get().getWinds().add(wind.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("Zuweisung der Windkraftanlage %s ist fehlgeschlagen, da die Anlage bereits zugewiesen ist", windEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Windkraftanlage %s konnte nicht gefunden werden", windEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("DK %s konnte nicht gefunden werden um Windkraftanlage abzufragen", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void assignToHousehold(WindEnergyEntity windEnergyEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneById(householdAggregate.getHouseholdId().getValue());
        if (household.isPresent()) {
            Optional<WindEnergy> WindEnergy = jpaRepository.findOneById(windEnergyEntity.getId().getValue());
            if (WindEnergy.isPresent()) {
                if (WindEnergy.get().getDecentralizedPowerPlant() == null &&
                        WindEnergy.get().getHousehold() == null) {
                    WindEnergy.get().setHousehold(household.get());
                    jpaRepository.save(WindEnergy.get());
                    household.get().getWinds().add(WindEnergy.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("Zuweisung der Windkraftanlage %s ist fehlgeschlagen, da dieser Anlage bereits zugewiesen ist", windEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Windkraftanlage %s konnte nicht gefunden werden", windEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("Haushalt %s konnte nicht gefunden werden um Windkraftanlage abzufragen", householdAggregate.getHouseholdId().getValue())
            );
        }
    }

    @Override
    public void deleteById(WindEnergyIdVO id) throws ProducerRepositoryException {
        Optional<WindEnergy> jpaEntity = jpaRepository.findOneById(id.getValue());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new ProducerRepositoryException(
                    String.format("Windkraftanlage %s konnte nicht gelöscht werden, da Wasserkraftanlage nicht gefunden werden konnte", id.getValue())
            );
        }
    }

    @Override
    public void update(WindEnergyIdVO id, WindEnergyEntity domainEntity) throws ProducerRepositoryException {
        Optional<WindEnergy> jpaEntityOptional = jpaRepository.findOneById(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            WindEnergy jpaEntity = jpaEntityOptional.get();
            WindEnergy updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setId(updated.getId());
            jpaEntity.setLongitude(updated.getLongitude());
            jpaEntity.setLatitude(updated.getLatitude());
            jpaEntity.setCapacity(updated.getCapacity());
            jpaEntity.setEfficiency(updated.getEfficiency());
            jpaEntity.setHeight(updated.getHeight());
            jpaEntity.setRadius(updated.getRadius());
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException("Windkraftanlage %s konnte nicht aktualisiert werden, da Wasserkraftanlage nicht gefunden wurde");
        }
    }

}
